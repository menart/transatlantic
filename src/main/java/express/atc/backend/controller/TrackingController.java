package express.atc.backend.controller;

import express.atc.backend.service.JwtService;
import express.atc.backend.service.TrackingService;
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

    @GetMapping("/find/{trackNumber}")
    public String findTrack(@PathVariable String trackNumber) {
        var token = getToken();
        String userPhone = token != null ? jwtService.extractPhone(token) : null;
        return trackingService.find(trackNumber, userPhone);
    }
}
