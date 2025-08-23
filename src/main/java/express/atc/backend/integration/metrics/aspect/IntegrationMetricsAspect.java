package express.atc.backend.integration.metrics.aspect;

import express.atc.backend.integration.metrics.annotation.IntegrationMetrics;
import express.atc.backend.integration.metrics.dto.IntegrationMetric;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class IntegrationMetricsAspect {

    private static final String METRIC_LOG_TEMPLATE = "INTEGRATION_METRIC - integration: {}, operation: {}, time: {}ms, success: {}";

    @Around("@annotation(integrationMetrics)")
    public Object measureIntegrationCall(ProceedingJoinPoint joinPoint, IntegrationMetrics integrationMetrics) throws Throwable {
        long startTime = System.currentTimeMillis();
        IntegrationMetric metric = new IntegrationMetric(
                integrationMetrics.integrationName(),
                integrationMetrics.operationName()
        );

        boolean success = false;
        Object result = null;

        try {
            // Логирование запроса если включено
            if (integrationMetrics.logRequest()) {
                logRequest(joinPoint, integrationMetrics);
            }

            result = joinPoint.proceed();
            success = true;

            // Логирование ответа если включено
            if (integrationMetrics.logResponse() && result != null) {
                logResponse(result, integrationMetrics);
            }

            return result;

        } catch (Exception e) {
            metric.setErrorMessage(e.getMessage());
            metric.setStatusCode("ERROR");
            throw e;

        } finally {
            long executionTime = System.currentTimeMillis() - startTime;
            metric.setExecutionTimeMs(executionTime);
            metric.setSuccess(success);

            if (result instanceof ResponseEntity) {
                ResponseEntity<?> response = (ResponseEntity<?>) result;
                metric.setStatusCode(String.valueOf(response.getStatusCode().value()));
            }

            logMetric(metric);
        }
    }

    private void logRequest(ProceedingJoinPoint joinPoint, IntegrationMetrics metrics) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String methodName = signature.getName();
        Object[] args = joinPoint.getArgs();

        log.debug("[{} - {}] Request - method: {}, args: {}",
                metrics.integrationName(),
                metrics.operationName(),
                methodName,
                Arrays.toString(args));
    }

    private void logResponse(Object result, IntegrationMetrics metrics) {
        if (result instanceof ResponseEntity) {
            ResponseEntity<?> response = (ResponseEntity<?>) result;
            log.debug("[{} - {}] Response - status: {}, body: {}",
                    metrics.integrationName(),
                    metrics.operationName(),
                    response.getStatusCode(),
                    response.getBody());
        } else {
            log.debug("[{} - {}] Response: {}",
                    metrics.integrationName(),
                    metrics.operationName(),
                    result);
        }
    }

    private void logMetric(IntegrationMetric metric) {
        if (metric.isSuccess()) {
            log.info(METRIC_LOG_TEMPLATE,
                    metric.getIntegrationName(),
                    metric.getOperationName(),
                    metric.getExecutionTimeMs(),
                    metric.isSuccess());
        } else {
            log.warn(METRIC_LOG_TEMPLATE + ", error: {}",
                    metric.getIntegrationName(),
                    metric.getOperationName(),
                    metric.getExecutionTimeMs(),
                    metric.isSuccess(),
                    metric.getErrorMessage());
        }

        // Здесь можно добавить отправку метрик в Prometheus, InfluxDB и т.д.
        sendToMonitoringSystem(metric);
    }

    private void sendToMonitoringSystem(IntegrationMetric metric) {
        // Реализация отправки метрик в систему мониторинга
        // Например: Micrometer, Prometheus, Elasticsearch и т.д.
    }
}