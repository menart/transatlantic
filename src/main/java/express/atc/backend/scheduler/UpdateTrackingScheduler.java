package express.atc.backend.scheduler;

import express.atc.backend.service.TrackingService;
import express.atc.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpdateTrackingScheduler {

    private final UserService userService;
    private final TrackingService trackingService;

    private Integer batchSize = 1000;

    @Scheduled(cron = "${service.tracking.update}")
    public void updateTracking() {
        Set<String> userPhone = userService.getBatchUserPhone(batchSize);
        userPhone.forEach(trackingService::updateListTracking);
    }
}
