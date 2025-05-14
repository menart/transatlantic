package express.atc.backend.controller;

import express.atc.backend.dto.*;
import express.atc.backend.enums.TrackingStatus;
import express.atc.backend.exception.TrackNotFoundException;
import express.atc.backend.service.TrackingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

import static express.atc.backend.integration.robokassa.config.RobokassaConfig.ROBOKASSA_ERROR_RESPONSE;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequiredArgsConstructor
@RequestMapping(path = "/api/tracking/", produces = "application/json")
@Tag(name = "Tracking controller", description = "Контроллер для отслеживания заказа")
public class TrackingController extends PrivateController {

    private final TrackingService trackingService;

    @Operation(summary = "Запросить информацию об отправлении по трек номеру или номеру заказа")
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
    @GetMapping("/find/{number}")
    public TrackingDto findTrack(
            @Parameter(description = "Трек-номер или номер заказа") @PathVariable String number)
            throws TrackNotFoundException {
        return trackingService.find(number);
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
        return trackingService.calc(trackNumber);
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
    public TrackingPageDto listTrack(
            @Parameter(description = "Номер страницы, начиная с 0")
            @RequestParam(name = "page", defaultValue = "0", required = false) Integer page,
            @Parameter(description = "Количество на странице")
            @RequestParam(name = "count", defaultValue = "10", required = false) Integer count,
            @Parameter(description = "Фильтрация запросов")
            @RequestParam(name = "filter", required = false) TrackingStatus filter) {
        return trackingService.list(page <= 0 ? 0 : page, count <= 1 ? 1 : count, filter);
    }

    @Operation(summary = "Запросить список трек-номеров требующих внимания")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Список трек-номеров требующих внимания",
                    useReturnTypeSchema = true,
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = TrackingNeedingDto.class))}),
            @ApiResponse(responseCode = "400",
                    description = "Невалидные параметры в запросе",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class))}),
            @ApiResponse(responseCode = "503",
                    description = ROBOKASSA_ERROR_RESPONSE,
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class))}),
    })
    @GetMapping("/need")
    public TrackingNeedingDto needTrack() {
        return trackingService.need();
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Загрузка документа в сервис",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Boolean.class))}),
            @ApiResponse(responseCode = "400",
                    description = "Невалидные параметры в запросе",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class))})
    })
    @PostMapping(path = "/upload/{trackNumber}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public boolean uploadDocuments(@RequestParam("file") MultipartFile file,
                                   @Parameter(description = "Трек-номер заказа") @PathVariable String trackNumber) {
        return trackingService.uploadFile(file, trackNumber);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Загрузка документов в сервис",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Boolean.class))}),
            @ApiResponse(responseCode = "400",
                    description = "Невалидные параметры в запросе",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class))})
    })
    @PostMapping(path = "/uploads/{trackNumber}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public boolean uploadsDocuments(@RequestBody MultipartFile[] files,
                                    @Parameter(description = "Трек-номер заказа") @PathVariable String trackNumber) {
        return trackingService.uploadFiles(files, trackNumber);
    }

    @GetMapping(path = "/all/{phoneNumber}")
    public Set<TrackingDto> loadList(@PathVariable String phoneNumber) {
        return trackingService.getAllTrackByPhone(phoneNumber);
    }

    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Установка статуса ожидания подтверждение оплаты",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class))}),
            @ApiResponse(responseCode = "404",
                    description = "Трек-номер не найден",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class))})
    })
    @GetMapping("/pay/{orderId}")
    public boolean paymentConfirmation(@Parameter(description = "Номер заказа") @PathVariable Long orderId)
            throws TrackNotFoundException {
        return trackingService.paymentConfirmation(orderId);
    }
}
