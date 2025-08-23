package express.atc.backend.metrics.service;

import io.micrometer.core.instrument.*;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class PrometheusMetricsService {

    private final PrometheusMeterRegistry prometheusRegistry;
    private final ConcurrentHashMap<String, Timer> timers = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Counter> counters = new ConcurrentHashMap<>();

    public void recordIntegrationMetric(String integrationName, String operationName,
                                        long durationMs, boolean success, String statusCode) {
        try {
            recordExecutionTime(integrationName, operationName, durationMs);
            recordTotalRequests(integrationName, operationName);
            recordSuccessFailure(integrationName, operationName, success);

            if (statusCode != null && !statusCode.equals("ERROR")) {
                recordStatusCode(integrationName, operationName, statusCode);
            }

        } catch (Exception e) {
            log.warn("Failed to record Prometheus metrics: {}", e.getMessage());
        }
    }

    private void recordExecutionTime(String integrationName, String operationName, long durationMs) {
        String timerName = "integration_operation_duration_seconds";

        Timer timer = timers.computeIfAbsent(getTimerKey(integrationName, operationName), key ->
                Timer.builder(timerName)
                        .description("Integration operation execution time")
                        .tags(
                                "integration", integrationName,
                                "operation", operationName,
                                "type", "external"
                        )
                        .publishPercentiles(0.5, 0.9, 0.95, 0.99)
                        .publishPercentileHistogram()
                        .register(prometheusRegistry)
        );

        timer.record(durationMs, TimeUnit.MILLISECONDS);
    }

    private void recordTotalRequests(String integrationName, String operationName) {
        String counterName = "integration_requests_total";

        Counter counter = counters.computeIfAbsent(getCounterKey(integrationName, operationName, "total"), key ->
                Counter.builder(counterName)
                        .description("Total integration requests count")
                        .tags(
                                "integration", integrationName,
                                "operation", operationName,
                                "direction", "outbound"
                        )
                        .register(prometheusRegistry)
        );

        counter.increment();
    }

    private void recordSuccessFailure(String integrationName, String operationName, boolean success) {
        String counterName = "integration_requests_result_total";

        Counter counter = counters.computeIfAbsent(
                getCounterKey(integrationName, operationName, "result_" + success), key ->
                        Counter.builder(counterName)
                                .description("Integration requests by result")
                                .tags(
                                        "integration", integrationName,
                                        "operation", operationName,
                                        "success", String.valueOf(success),
                                        "result", success ? "success" : "failure"
                                )
                                .register(prometheusRegistry)
        );

        counter.increment();
    }

    private void recordStatusCode(String integrationName, String operationName, String statusCode) {
        String counterName = "integration_requests_status_total";

        Counter counter = counters.computeIfAbsent(
                getCounterKey(integrationName, operationName, "status_" + statusCode), key ->
                        Counter.builder(counterName)
                                .description("Integration requests by HTTP status code")
                                .tags(
                                        "integration", integrationName,
                                        "operation", operationName,
                                        "status_code", statusCode,
                                        "status_category", getStatusCategory(statusCode)
                                )
                                .register(prometheusRegistry)
        );

        counter.increment();
    }

    private String getStatusCategory(String statusCode) {
        if (statusCode == null) return "unknown";
        int code = Integer.parseInt(statusCode);
        if (code < 200) return "1xx";
        if (code < 300) return "2xx";
        if (code < 400) return "3xx";
        if (code < 500) return "4xx";
        return "5xx";
    }

    public void recordError(String integrationName, String operationName, String errorType) {
        String counterName = "integration_errors_total";

        Counter counter = Counter.builder(counterName)
                .description("Integration errors by type")
                .tags(
                        "integration", integrationName,
                        "operation", operationName,
                        "error_type", errorType,
                        "severity", getErrorSeverity(errorType)
                )
                .register(prometheusRegistry);

        counter.increment();
    }

    private String getErrorSeverity(String errorType) {
        if (errorType.contains("Timeout") || errorType.contains("Connection")) {
            return "high";
        }
        if (errorType.contains("Validation") || errorType.contains("Client")) {
            return "medium";
        }
        return "low";
    }

    // Методы для мониторинга rate limiting
    public void recordRateLimit(String integrationName, String operationName, String limitType) {
        Counter.builder("integration_rate_limits_total")
                .description("Rate limit events")
                .tags(
                        "integration", integrationName,
                        "operation", operationName,
                        "limit_type", limitType
                )
                .register(prometheusRegistry)
                .increment();
    }

    // Методы для мониторинга retries
    public void recordRetry(String integrationName, String operationName, int attempt) {
        Counter.builder("integration_retries_total")
                .description("Retry attempts")
                .tags(
                        "integration", integrationName,
                        "operation", operationName,
                        "attempt", String.valueOf(attempt)
                )
                .register(prometheusRegistry)
                .increment();
    }

    // Методы для мониторинга времени установления соединения
    public void recordConnectionTime(String integrationName, String operationName, long connectionTimeMs) {
        Timer.builder("integration_connection_time_seconds")
                .description("Connection establishment time")
                .tags(
                        "integration", integrationName,
                        "operation", operationName
                )
                .register(prometheusRegistry)
                .record(connectionTimeMs, TimeUnit.MILLISECONDS);
    }

    private String getTimerKey(String integrationName, String operationName) {
        return integrationName + ":" + operationName + ":timer";
    }

    private String getCounterKey(String integrationName, String operationName, String type) {
        return integrationName + ":" + operationName + ":" + type;
    }

    // Метод для получения метрик в текстовом формате Prometheus
    public String scrape() {
        return prometheusRegistry.scrape();
    }
}