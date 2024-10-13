package express.atc.backend.controller;

import express.atc.backend.dto.ErrorResponseDto;
import express.atc.backend.dto.TrackingDto;
import express.atc.backend.exception.TrackNotFoundException;
import express.atc.backend.service.JwtService;
import express.atc.backend.service.TrackingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
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
    })
    @GetMapping("/find/{trackNumber}")
    public TrackingDto findTrack(@PathVariable String trackNumber) throws TrackNotFoundException {
        var token = getToken();
        String userPhone = token != null ? jwtService.extractPhone(token) : null;
        return trackingService.find(trackNumber, userPhone);
    }
}
