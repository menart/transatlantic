package express.atc.backend.metrics.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class PrometheusMetricsService {

    private final MeterRegistry meterRegistry;

    // Кэши для метрик с комбинированными ключами
    private final ConcurrentHashMap<String, Timer> timerCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Counter> counterCache = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Gauge> gaugeCache = new ConcurrentHashMap<>();

    /**
     * Основной метод для записи метрик интеграции
     */
    public void recordIntegrationMetric(String integrationName, String operationName,
                                        long durationMs, boolean success, String statusCode) {
        try {
            String baseTags = getBaseTags(integrationName, operationName);

            // Записываем все метрики с группировкой по integrationName и operationName
            recordExecutionTime(baseTags, integrationName, operationName, durationMs);
            recordTotalRequests(baseTags, integrationName, operationName);
            recordSuccessFailure(baseTags, integrationName, operationName, success);
            recordStatusCode(baseTags, integrationName, operationName, statusCode);
            recordCurrentExecutions(baseTags, integrationName, operationName, durationMs);

            log.debug("Metrics recorded for {}-{}: {}ms, success: {}",
                    integrationName, operationName, durationMs, success);

        } catch (Exception e) {
            log.warn("Failed to record Prometheus metrics: {}", e.getMessage());
        }
    }

    /**
     * Метод для записи ошибок с группировкой
     */
    public void recordError(String integrationName, String operationName, String errorType) {
        String baseTags = getBaseTags(integrationName, operationName);
        String counterKey = "error:" + baseTags + ":" + errorType;

        Counter counter = counterCache.computeIfAbsent(counterKey, key ->
                Counter.builder("integration_errors_total")
                        .description("Integration errors by type")
                        .tags(
                                "integration", integrationName,
                                "operation", operationName,
                                "error_type", errorType
                        )
                        .register(meterRegistry)
        );

        counter.increment();
    }

    /**
     * Запись времени выполнения с группировкой
     */
    private void recordExecutionTime(String baseTags, String integrationName,
                                     String operationName, long durationMs) {
        String timerKey = "execution:" + baseTags;

        Timer timer = timerCache.computeIfAbsent(timerKey, key ->
                Timer.builder("integration_operation_duration_seconds")
                        .description("Time spent for integration operations")
                        .tags(
                                "integration", integrationName,
                                "operation", operationName
                        )
                        .publishPercentiles(0.5, 0.95, 0.99)
                        .publishPercentileHistogram()
                        .register(meterRegistry)
        );

        timer.record(durationMs, TimeUnit.MILLISECONDS);
    }

    /**
     * Запись общего количества запросов с группировкой
     */
    private void recordTotalRequests(String baseTags, String integrationName, String operationName) {
        String counterKey = "total_requests:" + baseTags;

        Counter counter = counterCache.computeIfAbsent(counterKey, key ->
                Counter.builder("integration_requests_total")
                        .description("Total number of integration requests")
                        .tags(
                                "integration", integrationName,
                                "operation", operationName
                        )
                        .register(meterRegistry)
        );

        counter.increment();
    }

    /**
     * Запись успешных/неуспешных запросов с группировкой
     */
    private void recordSuccessFailure(String baseTags, String integrationName,
                                      String operationName, boolean success) {
        String counterKey = "success:" + baseTags + ":" + success;

        Counter counter = counterCache.computeIfAbsent(counterKey, key ->
                Counter.builder("integration_requests_result_total")
                        .description("Integration requests by result")
                        .tags(
                                "integration", integrationName,
                                "operation", operationName,
                                "success", String.valueOf(success)
                        )
                        .register(meterRegistry)
        );

        counter.increment();
    }

    /**
     * Запись статус кодов с группировкой
     */
    private void recordStatusCode(String baseTags, String integrationName,
                                  String operationName, String statusCode) {
        if (statusCode != null && !statusCode.equals("null")) {
            String counterKey = "status:" + baseTags + ":" + statusCode;

            Counter counter = counterCache.computeIfAbsent(counterKey, key ->
                    Counter.builder("integration_requests_status_total")
                            .description("Integration requests by HTTP status code")
                            .tags(
                                    "integration", integrationName,
                                    "operation", operationName,
                                    "status_code", statusCode
                            )
                            .register(meterRegistry)
            );

            counter.increment();
        }
    }

    /**
     * Gauge для текущего количества выполняющихся операций
     */
    private void recordCurrentExecutions(String baseTags, String integrationName,
                                         String operationName, long durationMs) {
        String gaugeKey = "current:" + baseTags;

        // Используем Function для обновления значения
        Gauge gauge = gaugeCache.computeIfAbsent(gaugeKey, key ->
                Gauge.builder("integration_current_executions", () -> 0.0)
                        .description("Current number of executing operations")
                        .tags(
                                "integration", integrationName,
                                "operation", operationName
                        )
                        .register(meterRegistry)
        );

        // Для gauge обычно используется callback, но мы можем обновлять через MeterRegistry
        meterRegistry.gauge("integration_current_executions",
                Tags.of(
                        "integration", integrationName,
                        "operation", operationName
                ),
                new AtomicDouble(1.0) // Пример значения
        );
    }

    /**
     * Генерация базовых тегов для группировки
     */
    private String getBaseTags(String integrationName, String operationName) {
        return integrationName + ":" + operationName;
    }

    /**
     * Дополнительные метрики с группировкой
     */
    public void recordRateLimit(String integrationName, String operationName) {
        String baseTags = getBaseTags(integrationName, operationName);
        String counterKey = "rate_limit:" + baseTags;

        Counter counter = counterCache.computeIfAbsent(counterKey, key ->
                Counter.builder("integration_rate_limits_total")
                        .description("Number of rate limit events")
                        .tags("integration", integrationName, "operation", operationName)
                        .register(meterRegistry)
        );

        counter.increment();
    }

    public void recordRetry(String integrationName, String operationName, int retryCount) {
        String baseTags = getBaseTags(integrationName, operationName);
        String counterKey = "retry:" + baseTags + ":" + retryCount;

        Counter counter = counterCache.computeIfAbsent(counterKey, key ->
                Counter.builder("integration_retries_total")
                        .description("Number of retry attempts")
                        .tags(
                                "integration", integrationName,
                                "operation", operationName,
                                "retry_count", String.valueOf(retryCount)
                        )
                        .register(meterRegistry)
        );

        counter.increment();
    }

    public void recordCacheHit(String integrationName, String operationName, boolean hit) {
        String baseTags = getBaseTags(integrationName, operationName);
        String counterKey = "cache:" + baseTags + ":" + (hit ? "hit" : "miss");

        Counter counter = counterCache.computeIfAbsent(counterKey, key ->
                Counter.builder("integration_cache_operations_total")
                        .description("Integration cache operations")
                        .tags(
                                "integration", integrationName,
                                "operation", operationName,
                                "cache_result", hit ? "hit" : "miss"
                        )
                        .register(meterRegistry)
        );

        counter.increment();
    }

    /**
     * Методы для получения метрик с группировкой
     */
    public double getTotalRequestsCount(String integrationName, String operationName) {
        return meterRegistry.counter("integration_requests_total",
                "integration", integrationName,
                "operation", operationName
        ).count();
    }

    public double getErrorCount(String integrationName, String operationName, String errorType) {
        return meterRegistry.counter("integration_errors_total",
                "integration", integrationName,
                "operation", operationName,
                "error_type", errorType
        ).count();
    }

    /**
     * Получение всех метрик для конкретной интеграции и операции
     */
    public IntegrationMetricsSummary getMetricsSummary(String integrationName, String operationName) {
        return new IntegrationMetricsSummary(
                integrationName,
                operationName,
                getTotalRequestsCount(integrationName, operationName),
                meterRegistry.timer("integration_operation_duration_seconds",
                        "integration", integrationName,
                        "operation", operationName
                ).totalTime(TimeUnit.MILLISECONDS),
                meterRegistry.counter("integration_requests_result_total",
                        "integration", integrationName,
                        "operation", operationName,
                        "success", "true"
                ).count()
        );
    }

    /**
     * DTO для сводки метрик
     */
    public record IntegrationMetricsSummary(
            String integrationName,
            String operationName,
            double totalRequests,
            double totalTimeMs,
            double successfulRequests
    ) {
        public double getSuccessRate() {
            return totalRequests > 0 ? (successfulRequests / totalRequests) * 100 : 0;
        }

        public double getAverageTimeMs() {
            return totalRequests > 0 ? totalTimeMs / totalRequests : 0;
        }
    }
}
