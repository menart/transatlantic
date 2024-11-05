package express.atc.backend.integration.cbrf.scheduler;

import express.atc.backend.integration.cbrf.service.CbrfService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CbrfScheduler {

    private final CbrfService cbrfService;

//        @Scheduled(cron = "${cbrf.scheduler.cron}")
    @Scheduled(initialDelay = 1000)
    public void updateCurrencyScheduler() {
        cbrfService.updateCurrency();
    }
}
