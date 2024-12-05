package express.atc.backend.controller;

import express.atc.backend.dto.*;
import express.atc.backend.enums.TrackingStatus;
import express.atc.backend.exception.TrackNotFoundException;
import express.atc.backend.service.JwtService;
import express.atc.backend.service.TrackingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static express.atc.backend.integration.robokassa.config.RobokassaConfig.ROBOKASSA_ERROR_RESPONSE;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
@RequestMapping(path = "/api/tracking/", produces = "application/json")
@Tag(name = "Tracking controller", description = "Контроллер для отслеживания заказа")
public class TrackingController extends PrivateController {

    private final TrackingService trackingService;
    private final JwtService jwtService;

    @Operation(summary = "Запросить информацию об отправлении")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Информация об отправлении",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TrackingDto.class))}),
            @ApiResponse(responseCode = "400",
                    description = "Невалидные параметры в запросе",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class))}),
            @ApiResponse(responseCode = "404",
                    description = "Трек-номер не найден",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class))}),
            @ApiResponse(responseCode = "503",
                    description = ROBOKASSA_ERROR_RESPONSE,
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class))}),
    })
    @GetMapping("/find/{trackNumber}")
    public TrackingDto findTrack(
            @Parameter(description = "Трек-номер заказа") @PathVariable String trackNumber)
            throws TrackNotFoundException {
        var token = getToken();
        String userPhone = token != null ? jwtService.extractPhone(token) : null;
        return trackingService.find(trackNumber, userPhone);
    }

    @Operation(summary = "Запросить расчет оплаты для отправлении")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Информация об оплате",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = CalculateDto.class))}),
            @ApiResponse(responseCode = "400",
                    description = "Невалидные параметры в запросе",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class))}),
            @ApiResponse(responseCode = "404",
                    description = "Трек-номер не найден",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class))}),
            @ApiResponse(responseCode = "503",
                    description = ROBOKASSA_ERROR_RESPONSE,
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class))}),
    })
    @GetMapping("/calc/{trackNumber}")
    public CalculateDto calcTrack(
            @Parameter(description = "Трек-номер заказа") @PathVariable String trackNumber)
            throws TrackNotFoundException {
        var token = getToken();
        String userPhone = token != null ? jwtService.extractPhone(token) : null;
        return trackingService.calc(trackNumber, userPhone);
    }

    @Operation(summary = "Запросить список страниц")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Информация об оплате",
                    useReturnTypeSchema = true,
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TrackingPageDto.class))}),
            @ApiResponse(responseCode = "400",
                    description = "Невалидные параметры в запросе",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class))}),
            @ApiResponse(responseCode = "404",
                    description = "Трек-номер не найден",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class))}),
            @ApiResponse(responseCode = "503",
                    description = ROBOKASSA_ERROR_RESPONSE,
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class))}),
    })
    @GetMapping("/list")
    public PageDto<TrackingDto> listTrack(
            @Parameter(description = "Номер страницы, начиная с 0")
            @RequestParam(name = "page", defaultValue = "0", required = false) Integer page,
            @Parameter(description = "Количество на странице")
            @RequestParam(name = "count", defaultValue = "10", required = false) Integer count,
            @Parameter(description = "Фильтрация запросов")
            @RequestParam(name = "filter", defaultValue = "active", required = false) TrackingStatus filter) {
        var token = getToken();
        String userPhone = token != null ? jwtService.extractPhone(token) : null;
        return trackingService.list(page <= 0 ? 0 : page, count <= 1 ? 1 : count, userPhone, filter);
    }
}
