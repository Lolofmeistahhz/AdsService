package com.example.userservice.controller;

import com.example.userservice.model.dto.UserDto;
import com.example.userservice.model.entity.User;
import com.example.userservice.service.UserService;
import com.google.gson.Gson;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Tag(name = "Контроллер по работе с пользователями", description = "API для управления пользователями")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;
    private final Gson gson;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
        this.gson = new Gson();
    }

    @Operation(summary = "Получение всех пользователей", description = "Возвращает список всех пользователей")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получение списка пользователей",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = User.class))),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
    })
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @Operation(summary = "Получение пользователя по ID", description = "Возвращает данные пользователя по его идентификатору")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получение пользователя",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден", content = @Content),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
    })
    @GetMapping("/{id}")
    public UserDto getUserById(
            @Parameter(description = "Идентификатор пользователя", required = true)
            @PathVariable("id") Integer id) {
        return userService.getUserById(id);
    }

    @Operation(summary = "Получение объявлений пользователя", description = "Возвращает список объявлений для указанного пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешное получение списка объявлений",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Map.class))),
            @ApiResponse(responseCode = "404", description = "Пользователь или объявления не найдены", content = @Content),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
    })
    @GetMapping("/ads")
    public List<Map<String, Object>> getAdsByUserId(
            @Parameter(description = "Идентификатор пользователя", required = true)
            @RequestParam("id") Integer id) {
        return userService.getAdsByUserId(id);
    }

    @Operation(summary = "Создание пользователя", description = "Создаёт нового пользователя на основе предоставленных данных")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пользователь успешно создан",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"message\": \"Пользователь успешно добавлен\"}"))),
            @ApiResponse(responseCode = "400", description = "Неверные данные запроса", content = @Content),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
    })
    @PostMapping
    public ResponseEntity<String> createUser(
            @Parameter(description = "Данные нового пользователя", required = true)
            @RequestBody UserDto userDto) {
        userService.createUser(userDto);
        Map<String, String> response = Map.of("message", "Пользователь успешно добавлен");
        String jsonResponse = gson.toJson(response);
        return ResponseEntity.status(HttpStatus.CREATED).body(jsonResponse);
    }

    @Operation(summary = "Обновление пользователя", description = "Обновляет данные существующего пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь успешно обновлён",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"message\": \"Пользователь обновлен успешно\"}"))),
            @ApiResponse(responseCode = "400", description = "Неверные данные запроса", content = @Content),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден", content = @Content),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
    })
    @PutMapping
    public ResponseEntity<String> updateUser(
            @Parameter(description = "Обновлённые данные пользователя", required = true)
            @RequestBody UserDto userDto) {
        userService.updateUser(userDto);
        Map<String, String> response = Map.of("message", "Пользователь обновлен успешно");
        String jsonResponse = gson.toJson(response);
        return ResponseEntity.ok(jsonResponse);
    }

    @Operation(summary = "Удаление пользователя", description = "Удаляет пользователя и связанные с ним объявления")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь и связанные объявления успешно удалены",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(example = "{\"message\": \"Пользователь и связанные объявления успешно удалены\"}"))),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден", content = @Content),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = @Content)
    })
    @DeleteMapping
    public ResponseEntity<String> deleteUser(
            @Parameter(description = "Идентификатор пользователя", required = true)
            @RequestParam("id") Integer id) {
        userService.deleteUser(id);
        Map<String, String> response = Map.of("message", "Пользователь и связанные объявления успешно удалены");
        String jsonResponse = gson.toJson(response);
        return ResponseEntity.ok(jsonResponse);
    }
}