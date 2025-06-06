package express.atc.backend.controller;

import express.atc.backend.dto.*;
import express.atc.backend.exception.ApiException;
import express.atc.backend.exception.AuthSmsException;
import express.atc.backend.helper.AuthHelper;
import express.atc.backend.mapper.UserMapper;
import express.atc.backend.model.AuthResponseModel;
import express.atc.backend.service.AuthService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static express.atc.backend.Constants.REFRESH_TOKEN;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(path = "/api/auth", produces = "application/json")
@Tag(name = "Authentication controller", description = "Контроллер для работы авторизацией пользователя")
public class AuthController {

    private final AuthService authService;
    private final HttpServletRequest request;
    private final UserMapper userMapper;

    @Value("${sms-aero.enable}")
    private boolean disabledGetSms;

    @Operation(summary = "Отправить сгенерированный код в смс пользователю")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Ответ количество секунд, через которые можно повторно отправить SMS",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Integer.class))}),
            @ApiResponse(responseCode = "400",
                    description = "Невалидные параметры в запросе",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class))}),
    })
    @PostMapping("/make")
    public int makeCode(@RequestBody @Valid AuthSmsDto authSmsDto) throws AuthSmsException {
        String ipAddress = request.getRemoteAddr();
        return authService.makeCode(ipAddress, authSmsDto);
    }

    @Operation(summary = "Проверка СМС")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Краткая информация о пользователе",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserShortDto.class))}),
            @ApiResponse(responseCode = "400",
                    description = "Невалидные параметры в запросе",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class))}),
            @ApiResponse(responseCode = "401",
                    description = " Ошибка авторизации, неверный код",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class))}),
    })
    @PostMapping("/validate")
    public UserShortDto validateCode(
            @RequestBody @Valid ValidateSmsDto validateSms,
            HttpServletRequest request,
            HttpServletResponse response) throws AuthSmsException {
        AuthResponseModel authResponse = authService.validateCode(validateSms);
        AuthHelper.setTokenCookie(response, authResponse.tokens() , request.isSecure());
        return userMapper.toShortDto(authResponse.user());
    }

    @GetMapping("/sms")
    @Hidden
    public ResponseEntity<String> getSms(@RequestParam String phone) {
        return disabledGetSms
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(authService.getSms(phone));
    }

    @Operation(summary = "Аутентификация по email/номеру телефона и паролю")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Краткая информация о пользователе",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserShortDto.class))}),
            @ApiResponse(responseCode = "400",
                    description = "Невалидные параметры в запросе",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class))}),
            @ApiResponse(responseCode = "401",
                    description = " Ошибка авторизации, неверный код",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class))}),
    })
    @PostMapping("/auth")
    public UserShortDto auth(
            @RequestBody @Valid LoginDto login,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws AuthSmsException {
        AuthResponseModel authResponse = authService.login(login);
        AuthHelper.setTokenCookie(response, authResponse.tokens(), request.isSecure());
        return userMapper.toShortDto(authResponse.user());
    }

    @Operation(summary = "Регистрация пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Краткая информация о пользователе",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserShortDto.class))}),
            @ApiResponse(responseCode = "400",
                    description = "Невалидные параметры в запросе",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class))}),
            @ApiResponse(responseCode = "401",
                    description = " Ошибка авторизации, неверный код",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class))}),
    })
    @PostMapping("/registration")
    public ResponseEntity<UserShortDto> registration(
            @RequestBody @Valid RegistrationDto registration,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ApiException {
        AuthResponseModel authResponse = authService.registration(registration);
        AuthHelper.setTokenCookie(response, authResponse.tokens() , request.isSecure());
        return new ResponseEntity<>(userMapper.toShortDto(authResponse.user()), HttpStatus.CREATED);
    }

    @Operation(summary = "Проверить номер телефона пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Ответ количество секунд, через которые можно повторно отправить SMS",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Integer.class))}),
            @ApiResponse(responseCode = "400",
                    description = "Невалидные параметры в запросе",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class))}),
    })
    @PostMapping("/check-phone")
    public int checkUserPhone(@RequestBody @Valid AuthSmsDto authSmsDto) throws AuthSmsException {
        String ipAddress = request.getRemoteAddr();
        return authService.checkUserPhone(ipAddress, authSmsDto);
    }

    @Operation(summary = "Выход пользователя из системы")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Результат выполнения",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Boolean.class))}),
            @ApiResponse(responseCode = "400",
                    description = "Невалидные параметры в запросе",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class))}),
    })
    @GetMapping("/logout")
    public boolean logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        try {
            String rawRefreshToken = AuthHelper.extractTokenFromCookie(REFRESH_TOKEN, request);
            if (rawRefreshToken != null) {
                UUID refreshToken = UUID.fromString(rawRefreshToken);
                authService.logout(refreshToken);
            }
        } catch (ApiException e) {
            // Логируем отсутствие токена, но продолжаем удаление куки
        } finally {
            AuthHelper.removeTokenCookie(response, request.isSecure());
        }
        return true;
    }
}
