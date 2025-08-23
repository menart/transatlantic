package express.atc.backend.metrics.controller;

import express.atc.backend.metrics.service.PrometheusMetricsService;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/management")
@RequiredArgsConstructor
public class PrometheusMetricsController {

    private final PrometheusMeterRegistry prometheusRegistry;
    private final PrometheusMetricsService metricsService;

    @GetMapping("/metrics")
    public String getMetrics() {
        return prometheusRegistry.scrape();
    }

    @GetMapping("/metrics/integration")
    public String getIntegrationMetrics() {
        return prometheusRegistry.scrape();
    }

    @GetMapping("/health")
    public String health() {
        return "{\"status\": \"UP\", \"metrics_port\": 8081}";
    }

    @GetMapping("/info")
    public String info() {
        return """
            {
                "application": "atc-backend",
                "metrics_port": 8081,
                "metrics_endpoint": "/management/metrics",
                "integration_metrics": true
            }
            """;
    }
}