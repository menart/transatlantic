package express.atc.backend.controller;

import express.atc.backend.dto.DeliveryDto;
import express.atc.backend.dto.ErrorResponseDto;
import express.atc.backend.service.LandingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(path = "/api/landing", produces = "application/json")
@Tag(name = "Landing controller", description = "Контроллер для работы с запросами с landing page")
public class LandingController {

    private final LandingService landingService;

    private final HttpServletRequest request;

    @Operation(summary = "Отправка заказа на доставку")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Заказ на доставку",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = DeliveryDto.class))}),
            @ApiResponse(responseCode = "400",
                    description = "Невалидные параметры в запросе",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class))}),
            @ApiResponse(responseCode = "503",
                    description = "Проблемы с отправкой почты",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class))}),
    })
    @PostMapping("/delivery")
    public boolean delivery(@Valid @RequestBody DeliveryDto delivery) throws MessagingException {

        return landingService.deliveryRequest(delivery);
    }
}
