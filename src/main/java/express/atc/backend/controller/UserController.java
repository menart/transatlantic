package express.atc.backend.controller;

import express.atc.backend.dto.ChangePasswordDto;
import express.atc.backend.dto.ErrorResponseDto;
import express.atc.backend.dto.LanguageDto;
import express.atc.backend.dto.UserDto;
import express.atc.backend.enums.Language;
import express.atc.backend.service.JwtService;
import express.atc.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
@RequestMapping(path = "/api/user/", produces = "application/json")
@Tag(name = "User controller", description = "Контроллер для работы с информацией о пользователе")
public class UserController extends PrivateController {

    private final UserService userService;
    private final JwtService jwtService;

    @Operation(summary = "Получить информацию о пользователе")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Информация о пользователе в базе",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class))}),
            @ApiResponse(responseCode = "400",
                    description = "Невалидные параметры в запросе",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class))}),
            @ApiResponse(responseCode = "404",
                    description = "Пользователь не найден в базе",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class))}),
    })
    @GetMapping("/me")
    public UserDto getFullUserInfo() {
        String userPhone = jwtService.extractPhone(getToken());
        return userService.findUserByPhone(userPhone);
    }

    @Operation(summary = "Обновить информацию о пользователе")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Информация обновленная в базе",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class))}),
            @ApiResponse(responseCode = "400",
                    description = "Невалидные параметры в запросе",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class))}),
            @ApiResponse(responseCode = "404",
                    description = "Пользователь не найден в базе",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class))}),
    })
    @PostMapping("/me")
    public UserDto updateFullUserInfo(@Valid @RequestBody UserDto userInfo) {
        String userPhone = jwtService.extractPhone(getToken());
        userInfo.setPhone(userPhone);
        return userService.updateFullUserInfo(userInfo);
    }

    @Operation(summary = "Удалить авторизованного пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Результат выполнения",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Boolean.class))}),
            @ApiResponse(responseCode = "400",
                    description = "Невалидные параметры в запросе",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class))}),
            @ApiResponse(responseCode = "404",
                    description = "Пользователь не найден в базе",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class))}),
    })
    @DeleteMapping("/me")
    public Boolean dropUser() {
        String userPhone = jwtService.extractPhone(getToken());
        return userService.dropUser(userPhone);
    }

    @Operation(summary = "Получить информацию о языке для пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Язык пользователя",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = LanguageDto.class))}),
            @ApiResponse(responseCode = "400",
                    description = "Невалидные параметры в запросе",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class))}),
            @ApiResponse(responseCode = "404",
                    description = "Пользователь не найден в базе",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class))}),
    })
    @GetMapping("/language")
    public LanguageDto getUserLanguage() {
        String userPhone = jwtService.extractPhone(getToken());
        return userService.getLanguage(userPhone);
    }

    @Operation(summary = "Сменить язык для пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Язык пользователя",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = LanguageDto.class))}),
            @ApiResponse(responseCode = "400",
                    description = "Невалидные параметры в запросе",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class))}),
            @ApiResponse(responseCode = "404",
                    description = "Пользователь не найден в базе",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class))}),
    })
    @PostMapping("/language/{language}")
    public LanguageDto setUserLanguage(@PathVariable Language language) {
        String userPhone = jwtService.extractPhone(getToken());
        return userService.setLanguage(userPhone, language);
    }

    @Operation(summary = "Сменить пароль для авторизованного пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Информация о пользователе в базе",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserDto.class))}),
            @ApiResponse(responseCode = "400",
                    description = "Невалидные параметры в запросе",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class))}),
            @ApiResponse(responseCode = "404",
                    description = "Пользователь не найден в базе",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class))}),
    })
    @PostMapping("/change-password")
    public UserDto changePassword(@Valid @RequestBody ChangePasswordDto changePassword) {
        String userPhone = jwtService.extractPhone(getToken());
        return userService.changePassword(userPhone, changePassword);
    }
}
