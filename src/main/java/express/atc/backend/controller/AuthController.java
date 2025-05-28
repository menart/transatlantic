package express.atc.backend.controller;

import express.atc.backend.dto.*;
import express.atc.backend.exception.ApiException;
import express.atc.backend.exception.AuthSmsException;
import express.atc.backend.service.AuthService;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(path = "/api/auth", produces = "application/json")
@Tag(name = "Authentication controller", description = "Контроллер для работы авторизацией пользователя")
public class AuthController {

    private final AuthService authService;
    private final HttpServletRequest request;

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
                    description = "Сгенерированный JWT токен",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = JwtAuthenticationResponse.class))}),
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
    public JwtAuthenticationResponse validateCode(@RequestBody @Valid ValidateSmsDto validateSms) throws AuthSmsException {
        return authService.validateCode(validateSms);
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
                    description = "Сгенерированный JWT токен",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = JwtAuthenticationResponse.class))}),
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
    public JwtAuthenticationResponse auth(@RequestBody @Valid LoginDto login) throws AuthSmsException {
        return authService.login(login);
    }

    @Operation(summary = "Регистрация пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Сгенерированный JWT токен",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = JwtAuthenticationResponse.class))}),
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
    public JwtAuthenticationResponse registration(@RequestBody @Valid RegistrationDto registration) throws ApiException {
        return authService.registration(registration);
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

    @Operation(summary = "Обновить access token по refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Сгенерированный JWT токен",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = JwtAuthenticationResponse.class))}),
            @ApiResponse(responseCode = "400",
                    description = "Невалидные параметры в запросе",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class))}),
            @ApiResponse(responseCode = "403",
                    description = " Ошибка авторизации, неверный refresh токен",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class))}),
    })
    @GetMapping("/refresh")
    public JwtAuthenticationResponse refresh(
            @RequestParam @Parameter(description = "refresh token") UUID refresh
    ) {
        return authService.refresh(refresh);
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
    public boolean logout(@RequestParam UUID refresh) {
        return authService.logout(refresh);
    }

}
