package com.example.adsservice.service;

import com.example.adsservice.exception.AdsException;
import com.example.adsservice.model.dto.AdsDto;
import com.example.adsservice.model.entity.Ads;
import com.example.adsservice.model.repository.AdsRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdsService {

    private static final Logger log = LoggerFactory.getLogger(AdsService.class);

    private final AdsRepository adsRepository;
    private final RestTemplate restTemplate;

    public List<AdsDto> getAllAds() {
        log.info("Fetching all ads from the database");
        List<Ads> ads = adsRepository.findAll();
        log.debug("Found {} ads", ads.size());
        return ads.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public AdsDto getAdById(Integer id) {
        log.info("Fetching ad with ID: {}", id);
        Optional<Ads> adOptional = adsRepository.findById(id);
        Ads ad = adOptional.orElseThrow(() -> {
            log.error("Ad with ID {} not found", id);
            return new AdsException("Объявление с ID " + id + " не найдено");
        });
        return convertToDto(ad);
    }

    public List<AdsDto> getAdsByUserId(Integer userId) {
        log.info("Fetching ads for user with ID: {}", userId);

        String userServiceUrl = "http://localhost:8089/users/" + userId;
        try {
            ResponseEntity<Map<String, Object>> userResponse = restTemplate.exchange(
                    userServiceUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Map<String, Object>>() {
                    }
            );
            if (userResponse.getStatusCode() != HttpStatus.OK) {
                log.warn("User with ID {} not found in UserService", userId);
                throw new AdsException("Пользователь с ID " + userId + " не найден");
            }
            log.debug("User with ID {} exists, proceeding to fetch ads", userId);
        } catch (RestClientException e) {
            log.error("Error while checking user ID: {} in UserService", userId, e);
            throw new AdsException("Ошибка при проверке пользователя: " + e.getMessage());
        }

        List<Ads> ads = adsRepository.findAll().stream()
                .filter(ad -> ad.getUserId().equals(userId))
                .collect(Collectors.toList());

        if (ads.isEmpty()) {
            log.warn("No ads found for user ID: {}", userId);
            throw new AdsException("У пользователя с ID " + userId + " нет объявлений");
        }

        return ads.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public void createAd(AdsDto adsDto) {
        log.info("Creating new ad with title: {}", adsDto.getTitle());

        String userServiceUrl = "http://localhost:8089/users/" + adsDto.getUserId();
        try {
            ResponseEntity<Map<String, Object>> userResponse = restTemplate.exchange(
                    userServiceUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Map<String, Object>>() {
                    }
            );
            if (userResponse.getStatusCode() != HttpStatus.OK) {
                log.warn("User with ID {} not found in UserService", adsDto.getUserId());
                throw new AdsException("Пользователь с ID " + adsDto.getUserId() + " не найден");
            }
            log.debug("User with ID {} exists, proceeding to create ad", adsDto.getUserId());
        } catch (RestClientException e) {
            log.error("Error while checking user ID: {} in UserService", adsDto.getUserId(), e);
            throw new AdsException("Ошибка при проверке пользователя: " + e.getMessage());
        }

        Ads ad = convertToEntity(adsDto);
        ad.setCreatedAt(LocalDateTime.now());
        adsRepository.save(ad);
        log.debug("Ad created with ID: {}", ad.getId());
    }

    public void updateAd(AdsDto adsDto) {
        log.info("Updating ad with ID: {}", adsDto.getId());
        Optional<Ads> adOptional = adsRepository.findById(adsDto.getId());
        Ads ad = adOptional.orElseThrow(() -> {
            log.error("Ad with ID {} not found for update", adsDto.getId());
            return new AdsException("Объявление с ID " + adsDto.getId() + " не найдено");
        });

        if (!ad.getUserId().equals(adsDto.getUserId())) {
            String userServiceUrl = "http://localhost:8089/users/" + adsDto.getUserId();
            try {
                ResponseEntity<Map<String, Object>> userResponse = restTemplate.exchange(
                        userServiceUrl,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<Map<String, Object>>() {
                        }
                );
                if (userResponse.getStatusCode() != HttpStatus.OK) {
                    log.warn("User with ID {} not found in UserService", adsDto.getUserId());
                    throw new AdsException("Пользователь с ID " + adsDto.getUserId() + " не найден");
                }
                log.debug("User with ID {} exists, proceeding to update ad", adsDto.getUserId());
            } catch (RestClientException e) {
                log.error("Error while checking user ID: {} in UserService", adsDto.getUserId(), e);
                throw new AdsException("Ошибка при проверке пользователя: " + e.getMessage());
            }
        }

        ad.setTitle(adsDto.getTitle());
        ad.setDescription(adsDto.getDescription());
        ad.setPrice(adsDto.getPrice());
        ad.setUserId(adsDto.getUserId());
        adsRepository.save(ad);
        log.debug("Ad with ID {} updated successfully", ad.getId());
    }

    public void deleteAd(Integer adId) {
        log.info("Deleting ad with ID: {}", adId);
        Optional<Ads> adOptional = adsRepository.findById(adId);
        Ads ad = adOptional.orElseThrow(() -> {
            log.error("Ad with ID {} not found for deletion", adId);
            return new AdsException("Объявление с ID " + adId + " не найдено");
        });
        adsRepository.delete(ad);
        log.debug("Ad with ID {} deleted successfully", adId);
    }

    public void deleteAdsByUserId(Integer userId) {
        log.info("Deleting ads for user with ID: {}", userId);


        String userServiceUrl = "http://localhost:8089/users/" + userId;
        try {
            ResponseEntity<Map<String, Object>> userResponse = restTemplate.exchange(
                    userServiceUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<Map<String, Object>>() {
                    }
            );
            if (userResponse.getStatusCode() != HttpStatus.OK) {
                log.warn("User with ID {} not found in UserService", userId);
                throw new AdsException("Пользователь с ID " + userId + " не найден");
            }
            log.debug("User with ID {} exists, proceeding to delete ads", userId);
        } catch (RestClientException e) {
            log.error("Error while checking user ID: {} in UserService", userId, e);
            throw new AdsException("Ошибка при проверке пользователя: " + e.getMessage());
        }


        List<Ads> ads = adsRepository.findAll().stream()
                .filter(ad -> ad.getUserId().equals(userId))
                .collect(Collectors.toList());

        if (ads.isEmpty()) {
            log.warn("No ads found for user ID: {}", userId);
            throw new AdsException("Объявления пользователя с ID " + userId + " не найдены");
        }

        adsRepository.deleteAll(ads);
        log.debug("Deleted {} ads for user ID: {}", ads.size(), userId);
    }

    private AdsDto convertToDto(Ads ad) {
        return AdsDto.builder()
                .id(ad.getId())
                .title(ad.getTitle())
                .description(ad.getDescription())
                .price(ad.getPrice())
                .userId(ad.getUserId())
                .createdAt(ad.getCreatedAt())
                .build();
    }

    private Ads convertToEntity(AdsDto adsDto) {
        return Ads.builder()
                .id(adsDto.getId())
                .title(adsDto.getTitle())
                .description(adsDto.getDescription())
                .price(adsDto.getPrice())
                .userId(adsDto.getUserId())
                .createdAt(adsDto.getCreatedAt())
                .build();
    }
}