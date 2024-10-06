package express.atc.backend.controller;

import express.atc.backend.dto.AuthSmsDto;
import express.atc.backend.dto.ErrorResponseDto;
import express.atc.backend.dto.JwtAuthenticationResponse;
import express.atc.backend.dto.ValidateSmsDto;
import express.atc.backend.exception.AuthSmsException;
import express.atc.backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/auth", produces = "application/json")
@Tag(name = "Authentication controller", description = "Контроллер для работы авторизацией пользователя")
public class AuthController {

    private final AuthService authService;
    private final HttpServletRequest request;

    @Operation(summary = "Отправить сгенирированный код в смс пользователю")
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
                    description = "Сгенирированый JWT токен",
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
    //@ConditionalOnExpression("${smsAero.enabled:false}!='true'")
    public String getSms(@RequestParam String phone){
        return authService.getSms(phone);
    }
}
