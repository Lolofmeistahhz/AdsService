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
@RequestMapping("/users")
@Tag(name = "User API", description = "API для управления пользователями")
public class UserProxyController {

    private final RestTemplate restTemplate;
    private static final String USER_SERVICE_URL = "http://localhost:8089/users";

    @Autowired
    public UserProxyController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Operation(summary = "Получить всех пользователей", description = "Возвращает список всех пользователей")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получение списка пользователей",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type = "array", example = "[{\"id\": 1, \"name\": \"John Doe\"}]"))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
    })
    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        try {
            ResponseEntity<Object> response = restTemplate.getForEntity(USER_SERVICE_URL, Object.class);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (HttpClientErrorException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ошибка при получении списка пользователей: " + ex.getMessage()));
        }
    }

    @Operation(summary = "Получение пользователя по ID", description = "Возвращает данные пользователя по его идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получение пользователя",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"id\": 1, \"name\": \"John Doe\"}"))),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден", content = @Content),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
    })
    @GetMapping("/{id}")
    public ResponseEntity<Object> getUserById(
            @Parameter(description = "Идентификатор пользователя", required = true)
            @PathVariable Integer id) {
        try {
            ResponseEntity<Object> response = restTemplate.getForEntity(USER_SERVICE_URL + "/{id}", Object.class, id);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (HttpClientErrorException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ошибка при получении пользователя: " + ex.getMessage()));
        }
    }

    @Operation(summary = "Создать нового пользователя", description = "Создаёт нового пользователя на основе предоставленных данных")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пользователь успешно создан",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"Пользователь успешно добавлен\"}"))),
            @ApiResponse(responseCode = "400", description = "Неверные данные запроса", content = @Content),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
    })
    @PostMapping
    public ResponseEntity<Object> createUser(
            @Parameter(description = "Данные нового пользователя (например, {\"name\": \"John Doe\"})", required = true)
            @RequestBody Object requestBody) {
        try {
            ResponseEntity<Object> response = restTemplate.postForEntity(USER_SERVICE_URL, requestBody, Object.class);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (HttpClientErrorException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ошибка при создании пользователя: " + ex.getMessage()));
        }
    }

    @Operation(summary = "Обновить данные пользователя", description = "Обновляет данные существующего пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно обновлён",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"Пользователь обновлен успешно\"}"))),
            @ApiResponse(responseCode = "400", description = "Неверные данные запроса", content = @Content),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден", content = @Content),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
    })
    @PutMapping
    public ResponseEntity<Object> updateUser(
            @Parameter(description = "Обновлённые данные пользователя (например, {\"id\": 1, \"name\": \"Jane Doe\"})", required = true)
            @RequestBody Object requestBody) {
        try {
            ResponseEntity<Object> response = restTemplate.exchange(
                    USER_SERVICE_URL,
                    HttpMethod.PUT,
                    new HttpEntity<>(requestBody),
                    Object.class
            );
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (HttpClientErrorException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ошибка при обновлении пользователя: " + ex.getMessage()));
        }
    }

    @Operation(summary = "Удалить пользователя по ID", description = "Удаляет пользователя и связанные с ним объявления")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь и связанные объявления успешно удалены",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"Пользователь и связанные объявления успешно удалены\"}"))),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден", content = @Content),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUserById(
            @Parameter(description = "Идентификатор пользователя", required = true)
            @PathVariable Integer id) {
        try {
            ResponseEntity<Object> response = restTemplate.exchange(
                    USER_SERVICE_URL + "?id={id}",
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
                    .body(Map.of("error", "Ошибка при удалении пользователя: " + ex.getMessage()));
        }
    }

    @Operation(summary = "Получить объявления пользователя по ID", description = "Возвращает список объявлений для указанного пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получение списка объявлений",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(type = "array", example = "[{\"id\": 1, \"title\": \"Sample Ad\", \"description\": \"A sample ad\", \"price\": 100.0, \"userId\": 1, \"createdAt\": \"2023-10-01T10:00:00\"}]"))),
            @ApiResponse(responseCode = "404", description = "Пользователь или объявления не найдены", content = @Content),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
    })
    @GetMapping("/ads")
    public ResponseEntity<Object> getUserAds(
            @Parameter(description = "Идентификатор пользователя", required = true)
            @RequestParam Integer id) {
        try {
            ResponseEntity<Object> response = restTemplate.getForEntity(
                    USER_SERVICE_URL + "/ads?id={id}",
                    Object.class,
                    id
            );
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (HttpClientErrorException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(Map.of("error", ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Ошибка при получении объявлений пользователя: " + ex.getMessage()));
        }
    }
}