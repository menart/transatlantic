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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/auth/", produces = "application/json")
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

    @PostMapping("/validate")
    public JwtAuthenticationResponse validateCode(@RequestBody @Valid ValidateSmsDto validateSms) throws AuthSmsException {
        return authService.validateCode(validateSms);
    }
}
