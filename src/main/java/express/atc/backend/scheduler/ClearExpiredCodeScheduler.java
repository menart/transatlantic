package express.atc.backend.scheduler;

import express.atc.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class ClearExpiredCodeScheduler {

    private final AuthService authService;

    @Scheduled(fixedDelayString = "${auth.scheduler.interval}", timeUnit = TimeUnit.SECONDS)
    public void clearExpiredCode() {
        authService.clearExpired();
    }
}
