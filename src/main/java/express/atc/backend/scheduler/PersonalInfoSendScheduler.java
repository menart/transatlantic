package express.atc.backend.scheduler;

import express.atc.backend.service.TrackingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
@Slf4j
public class PersonalInfoSendScheduler {

    private final TrackingService trackingService;

    @Value("${personal.send-info.batch-size}")
    public Integer infoSendBatchSize;

    @Scheduled(fixedDelayString = "${personal.send-info.interval}", timeUnit = TimeUnit.SECONDS)
    public void sendPersonalInfo() {
        trackingService.sendUserInfoBatch(infoSendBatchSize);
    }
}
