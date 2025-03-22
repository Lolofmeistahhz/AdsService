package com.example.springgateway.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/ads")
@Tag(name = "Ads API", description = "API для управления объявлениями")
public class AdsProxyController {

    private final RestTemplate restTemplate;
    private static final String ADS_SERVICE_URL = "http://localhost:8080/ads";

    @Autowired
    public AdsProxyController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Operation(summary = "Получить все объявления", description = "Возвращает список всех объявлений")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получение списка объявлений",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type = "array", example = "[{\"id\": 1, \"title\": \"Sample Ad\", \"description\": \"A sample ad\", \"price\": 100.0, \"userId\": 1, \"createdAt\": \"2023-10-01T10:00:00\"}]"))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
    })
    @GetMapping
    public ResponseEntity<Object> getAllAds() {
        try {
            ResponseEntity<Object> response = restTemplate.getForEntity(ADS_SERVICE_URL, Object.class);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (HttpClientErrorException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ошибка при получении списка объявлений: " + ex.getMessage()));
        }
    }

    @Operation(summary = "Получить объявление по ID", description = "Возвращает данные объявления по его идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получение объявления",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"id\": 1, \"title\": \"Sample Ad\", \"description\": \"A sample ad\", \"price\": 100.0, \"userId\": 1, \"createdAt\": \"2023-10-01T10:00:00\"}"))),
            @ApiResponse(responseCode = "404", description = "Объявление не найдено", content = @Content),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Object> getAdById(
            @Parameter(description = "Идентификатор объявления", required = true)
            @PathVariable Integer id) {
        try {
            ResponseEntity<Object> response = restTemplate.getForEntity(ADS_SERVICE_URL + "/{id}", Object.class, id);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (HttpClientErrorException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ошибка при получении объявления: " + ex.getMessage()));
        }
    }

    @Operation(summary = "Получение объявлений пользователя", description = "Возвращает список объявлений для указанного пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получение списка объявлений",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type = "array", example = "[{\"id\": 1, \"title\": \"Sample Ad\", \"description\": \"A sample ad\", \"price\": 100.0, \"userId\": 1, \"createdAt\": \"2023-10-01T10:00:00\"}]"))),
            @ApiResponse(responseCode = "404", description = "Пользователь или объявления не найдены", content = @Content),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
    })
    @GetMapping("/by-user")
    public ResponseEntity<Object> getAdsByUserId(
            @Parameter(description = "Идентификатор пользователя", required = true)
            @RequestParam("userId") Integer userId) {
        try {
            ResponseEntity<Object> response = restTemplate.getForEntity(
                    ADS_SERVICE_URL + "/by-user?userId={userId}",
                    Object.class,
                    userId
            );
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (HttpClientErrorException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ошибка при получении объявлений пользователя: " + ex.getMessage()));
        }
    }

    @Operation(summary = "Создать новое объявление", description = "Создаёт новое объявление на основе предоставленных данных")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Объявление успешно создано",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"Объявление успешно создано\"}"))),
            @ApiResponse(responseCode = "400", description = "Неверные данные запроса", content = @Content),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден", content = @Content),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Object> createAd(
            @Parameter(description = "Данные нового объявления (например, {\"title\": \"New Ad\", \"description\": \"A new ad\", \"price\": 150.0, \"userId\": 1})", required = true)
            @RequestBody Object requestBody) {
        try {
            ResponseEntity<Object> response = restTemplate.postForEntity(ADS_SERVICE_URL, requestBody, Object.class);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (HttpClientErrorException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ошибка при создании объявления: " + ex.getMessage()));
        }
    }

    @Operation(summary = "Обновить данные объявления", description = "Обновляет данные существующего объявления")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Объявление успешно обновлено",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"Объявление успешно обновлено\"}"))),
            @ApiResponse(responseCode = "400", description = "Неверные данные запроса", content = @Content),
            @ApiResponse(responseCode = "404", description = "Объявление или пользователь не найдены", content = @Content),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
    })
    @PutMapping
    public ResponseEntity<Object> updateAd(
            @Parameter(description = "Обновлённые данные объявления (например, {\"id\": 1, \"title\": \"Updated Ad\", \"description\": \"Updated description\", \"price\": 200.0, \"userId\": 1})", required = true)
            @RequestBody Object requestBody) {
        try {
            ResponseEntity<Object> response = restTemplate.exchange(
                    ADS_SERVICE_URL,
                    HttpMethod.PUT,
                    new HttpEntity<>(requestBody),
                    Object.class
            );
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (HttpClientErrorException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ошибка при обновлении объявления: " + ex.getMessage()));
        }
    }

    @Operation(summary = "Удалить объявление по ID", description = "Удаляет объявление по его идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Объявление успешно удалено",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"Объявление успешно удалено\"}"))),
            @ApiResponse(responseCode = "404", description = "Объявление не найдено", content = @Content),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteAdById(
            @Parameter(description = "Идентификатор объявления", required = true)
            @PathVariable Integer id) {
        try {
            ResponseEntity<Object> response = restTemplate.exchange(
                    ADS_SERVICE_URL + "/{id}",
                    HttpMethod.DELETE,
                    null,
                    Object.class,
                    id
            );
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (HttpClientErrorException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ошибка при удалении объявления: " + ex.getMessage()));
        }
    }

    @Operation(summary = "Удалить все объявления пользователя по ID", description = "Удаляет все объявления, связанные с указанным пользователем")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Объявления успешно удалены",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"Все объявления пользователя успешно удалены\"}"))),
            @ApiResponse(responseCode = "404", description = "Пользователь или объявления не найдены", content = @Content),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
    })
    @DeleteMapping("/by-user")
    public ResponseEntity<Object> deleteUserAds(
            @Parameter(description = "Идентификатор пользователя", required = true)
            @RequestParam("userId") Integer userId) {
        try {
            ResponseEntity<Object> response = restTemplate.exchange(
                    ADS_SERVICE_URL + "/by-user?userId={userId}",
                    HttpMethod.DELETE,
                    null,
                    Object.class,
                    userId
            );
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (HttpClientErrorException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ошибка при удалении объявлений пользователя: " + ex.getMessage()));
        }
    }
}