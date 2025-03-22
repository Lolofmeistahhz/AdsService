package com.example.userservice.service;

import com.example.userservice.exception.UserException;
import com.example.userservice.model.dto.UserDto;
import com.example.userservice.model.entity.User;
import com.example.userservice.model.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    public List<User> getAllUsers() {
        log.info("Fetching all users from the database");
        List<User> users = userRepository.findAll();
        log.debug("Found {} users", users.size());
        return users;
    }

    public UserDto getUserById(Integer id) {
        log.info("Fetching user with ID: {}", id);
        Optional<User> userOptional = userRepository.findById(id);
        User user = userOptional.orElseThrow(() -> {
            log.error("User with ID {} not found", id);
            return new UserException("Пользователь с ID " + id + " не найден");
        });
        return convertToDto(user);
    }

    public List<Map<String, Object>> getAdsByUserId(Integer userId) {
        log.info("Fetching ads for user with ID: {} from AdsService", userId);
        String url = "http://localhost:8080/ads?userId=" + userId;
        try {
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );
            if (response.getStatusCode() == HttpStatus.OK) {
                List<Map<String, Object>> ads = response.getBody();
                log.debug("Successfully fetched {} ads for user ID: {}", ads != null ? ads.size() : 0, userId);
                return ads;
            } else if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                log.warn("No ads found for user ID: {}", userId);
                return List.of(); 
            }
            log.error("Failed to fetch ads for user ID: {}. Status code: {}", userId, response.getStatusCode());
            throw new UserException("Ошибка во время получения объявлений для пользователя: " + response.getStatusCode());
        } catch (RestClientException e) {
            log.error("Error while fetching ads for user ID: {} from AdsService", userId, e);
            throw new UserException("Ошибка при отправке запроса для получения объявлений: " + e.getMessage());
        }
    }

    public void createUser(UserDto userDto) {
        log.info("Creating new user: {}", userDto.getUsername());
        User user = convertToEntity(userDto);
        userRepository.save(user);
        log.debug("User created with ID: {}", user.getId());
    }

    public void updateUser(UserDto userDto) {
        log.info("Updating user with ID: {}", userDto.getId());
        Optional<User> userOptional = userRepository.findById(userDto.getId());
        User user = userOptional.orElseThrow(() -> {
            log.error("User with ID {} not found for update", userDto.getId());
            return new UserException("Пользователь с ID " + userDto.getId() + " не найден");
        });
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPassword(userDto.getPassword());
        userRepository.save(user);
        log.debug("User with ID {} updated successfully", user.getId());
    }

    public void deleteUser(Integer userId) {
        log.info("Deleting user with ID: {}", userId);
        String url = "http://localhost:8080/ads?userId=" + userId;
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.DELETE,
                    null,
                    String.class
            );
            if (response.getStatusCode() == HttpStatus.OK) {
                log.debug("Ads deleted for user ID: {}", userId);
            } else if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                log.warn("No ads found to delete for user ID: {}", userId);
            } else {
                log.error("Failed to delete ads for user ID: {}. Status code: {}", userId, response.getStatusCode());
                throw new UserException("Ошибка при удалении связанных объявлений: " + response.getStatusCode());
            }
        } catch (RestClientException e) {
            log.error("Error while deleting ads for user ID: {} from AdsService", userId, e);
            throw new UserException("Ошибка при удалении объявлений: " + e.getMessage());
        }

        Optional<User> userOptional = userRepository.findById(userId);
        User user = userOptional.orElseThrow(() -> {
            log.error("User with ID {} not found for deletion", userId);
            return new UserException("Пользователь с ID " + userId + " не найден");
        });
        userRepository.delete(user);
        log.debug("User with ID {} deleted successfully", userId);
    }

    private UserDto convertToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .password(user.getPassword())
                .build();
    }

    private User convertToEntity(UserDto userDto) {
        return User.builder()
                .id(userDto.getId())
                .username(userDto.getUsername())
                .email(userDto.getEmail())
                .password(userDto.getPassword())
                .build();
    }
}