package express.atc.backend.metrics.aspect;

import express.atc.backend.metrics.annotation.IntegrationMetrics;
import express.atc.backend.metrics.dto.IntegrationMetric;
import express.atc.backend.metrics.service.PrometheusMetricsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static express.atc.backend.Constants.METRIC_LOG_TEMPLATE;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class IntegrationMetricsAspect {

    private final PrometheusMetricsService prometheusMetricsService;

    // Счетчик текущих выполняющихся операций по integrationName и operationName
    private final ConcurrentHashMap<String, AtomicInteger> currentExecutions = new ConcurrentHashMap<>();

    @Around("@annotation(integrationMetrics)")
    public Object measureIntegrationCall(ProceedingJoinPoint joinPoint, IntegrationMetrics integrationMetrics) throws Throwable {
        String integrationName = integrationMetrics.integrationName();
        String operationName = integrationMetrics.operationName();
        String executionKey = getExecutionKey(integrationName, operationName);

        // Увеличиваем счетчик текущих выполнений
        currentExecutions.computeIfAbsent(executionKey, k -> new AtomicInteger(0)).incrementAndGet();

        long startTime = System.currentTimeMillis();
        IntegrationMetric metric = new IntegrationMetric(integrationName, operationName);

        boolean success = false;
        Object result = null;
        String statusCode = null;

        try {
            if (integrationMetrics.logRequest()) {
                logRequest(joinPoint, integrationMetrics);
            }

            result = joinPoint.proceed();
            success = true;

            if (result instanceof ResponseEntity) {
                ResponseEntity<?> response = (ResponseEntity<?>) result;
                statusCode = String.valueOf(response.getStatusCode().value());
                metric.setStatusCode(statusCode);
            }

            if (integrationMetrics.logResponse() && result != null) {
                logResponse(result, integrationMetrics);
            }

            return result;

        } catch (Exception e) {
            metric.setErrorMessage(e.getMessage());
            metric.setStatusCode("ERROR");

            prometheusMetricsService.recordError(
                    integrationName,
                    operationName,
                    e.getClass().getSimpleName()
            );

            throw e;

        } finally {
            // Уменьшаем счетчик текущих выполнений
            currentExecutions.get(executionKey).decrementAndGet();

            long executionTime = System.currentTimeMillis() - startTime;
            metric.setExecutionTimeMs(executionTime);
            metric.setSuccess(success);

            // Отправляем все метрики с группировкой
            prometheusMetricsService.recordIntegrationMetric(
                    integrationName,
                    operationName,
                    executionTime,
                    success,
                    statusCode
            );

            logMetric(metric);
        }
    }

    private String getExecutionKey(String integrationName, String operationName) {
        return integrationName + ":" + operationName;
    }

    public int getCurrentExecutionsCount(String integrationName, String operationName) {
        String key = getExecutionKey(integrationName, operationName);
        AtomicInteger count = currentExecutions.get(key);
        return count != null ? count.get() : 0;
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
    }
}