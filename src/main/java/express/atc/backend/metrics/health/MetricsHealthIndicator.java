package express.atc.backend.metrics.health;

import express.atc.backend.metrics.service.PrometheusMetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MetricsHealthIndicator implements HealthIndicator {

    private final PrometheusMetricsService metricsService;

    @Override
    public Health health() {
        try {
            String metrics = metricsService.scrape();
            if (metrics.contains("integration_requests_total")) {
                return Health.up()
                        .withDetail("port", 8081)
                        .withDetail("metrics_available", true)
                        .withDetail("endpoint", "/management/metrics")
                        .build();
            }
            return Health.down()
                    .withDetail("error", "No integration metrics found")
                    .build();
        } catch (Exception e) {
            return Health.down(e)
                    .withDetail("port", 8081)
                    .withDetail("error", "Metrics service unavailable")
                    .build();
        }
    }
}