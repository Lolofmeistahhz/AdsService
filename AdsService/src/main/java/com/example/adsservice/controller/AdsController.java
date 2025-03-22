package com.example.adsservice.controller;

import com.example.adsservice.model.dto.AdsDto;
import com.example.adsservice.service.AdsService;
import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ads")
@Tag(name = "Контроллер по работе с объявлениями", description = "API для управления объявлениями")
@RequiredArgsConstructor
public class AdsController {
    private static final Logger log = LoggerFactory.getLogger(AdsController.class);

    private  AdsService adsService;
    private  Gson gson;

    @Autowired
    public AdsController(AdsService adsService) {
        this.adsService = adsService;
        this.gson = new Gson();
    }

    @Operation(summary = "Получение всех объявлений", description = "Возвращает список всех объявлений")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получение списка объявлений",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AdsDto.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
    })
    @GetMapping
    public List<AdsDto> getAllAds() {
        log.info("Received request to get all ads");
        return adsService.getAllAds();
    }

    @Operation(summary = "Получение объявления по ID", description = "Возвращает данные объявления по его идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получение объявления",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AdsDto.class))),
            @ApiResponse(responseCode = "404", description = "Объявление не найдено", content = @Content),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
    })
    @GetMapping("/{id}")
    public AdsDto getAdById(
            @Parameter(description = "Идентификатор объявления", required = true)
            @PathVariable("id") Integer id) {
        log.info("Received request to get ad by ID: {}", id);
        return adsService.getAdById(id);
    }

    @Operation(summary = "Получение объявлений пользователя", description = "Возвращает список объявлений для указанного пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получение списка объявлений",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AdsDto.class))),
            @ApiResponse(responseCode = "404", description = "Пользователь или объявления не найдены", content = @Content),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
    })
    @GetMapping("/by-user")
    public List<AdsDto> getAdsByUserId(
            @Parameter(description = "Идентификатор пользователя", required = true)
            @RequestParam("userId") Integer userId) {
        log.info("Received request to get ads for user ID: {}", userId);
        return adsService.getAdsByUserId(userId);
    }

    @Operation(summary = "Создание объявления", description = "Создаёт новое объявление на основе предоставленных данных")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Объявление успешно создано",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"message\": \"Объявление успешно создано\"}"))),
            @ApiResponse(responseCode = "400", description = "Неверные данные запроса", content = @Content),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден", content = @Content),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
    })
    @PostMapping
    public ResponseEntity<String> createAd(
            @Parameter(description = "Данные нового объявления", required = true)
            @RequestBody AdsDto adsDto) {
        log.info("Received request to create ad with title: {}", adsDto.getTitle());
        adsService.createAd(adsDto);
        Map<String, String> response = Map.of("message", "Объявление успешно создано");
        String jsonResponse = gson.toJson(response);
        return ResponseEntity.status(HttpStatus.CREATED).body(jsonResponse);
    }

    @Operation(summary = "Обновление объявления", description = "Обновляет данные существующего объявления")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Объявление успешно обновлено",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"message\": \"Объявление успешно обновлено\"}"))),
            @ApiResponse(responseCode = "400", description = "Неверные данные запроса", content = @Content),
            @ApiResponse(responseCode = "404", description = "Объявление или пользователь не найдены", content = @Content),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
    })
    @PutMapping
    public ResponseEntity<String> updateAd(
            @Parameter(description = "Обновлённые данные объявления", required = true)
            @RequestBody AdsDto adsDto) {
        log.info("Received request to update ad with ID: {}", adsDto.getId());
        adsService.updateAd(adsDto);
        Map<String, String> response = Map.of("message", "Объявление успешно обновлено");
        String jsonResponse = gson.toJson(response);
        return ResponseEntity.ok(jsonResponse);
    }

    @Operation(summary = "Удаление объявления по ID", description = "Удаляет объявление по его идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Объявление успешно удалено",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"message\": \"Объявление успешно удалено\"}"))),
            @ApiResponse(responseCode = "404", description = "Объявление не найдено", content = @Content),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAd(
            @Parameter(description = "Идентификатор объявления", required = true)
            @PathVariable("id") Integer id) {
        log.info("Received request to delete ad with ID: {}", id);
        adsService.deleteAd(id);
        Map<String, String> response = Map.of("message", "Объявление успешно удалено");
        String jsonResponse = gson.toJson(response);
        return ResponseEntity.ok(jsonResponse);
    }

    @Operation(summary = "Удаление всех объявлений пользователя", description = "Удаляет все объявления, связанные с указанным пользователем")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Объявления успешно удалены",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"message\": \"Все объявления пользователя успешно удалены\"}"))),
            @ApiResponse(responseCode = "404", description = "Пользователь или объявления не найдены", content = @Content),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
    })
    @DeleteMapping("/by-user")
    public ResponseEntity<String> deleteAdsByUserId(
            @Parameter(description = "Идентификатор пользователя", required = true)
            @RequestParam("userId") Integer userId) {
        log.info("Received request to delete all ads for user ID: {}", userId);
        adsService.deleteAdsByUserId(userId);
        Map<String, String> response = Map.of("message", "Все объявления пользователя успешно удалены");
        String jsonResponse = gson.toJson(response);
        return ResponseEntity.ok(jsonResponse);
    }
}