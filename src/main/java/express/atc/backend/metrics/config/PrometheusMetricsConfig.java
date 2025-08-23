package express.atc.backend.metrics.config;

import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.prometheusmetrics.PrometheusConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class PrometheusMetricsConfig {

    @Bean
    @Primary
    public PrometheusMeterRegistry prometheusMeterRegistry() {
        PrometheusMeterRegistry registry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);

        // Конфигурируем registry
        registry.config().meterFilter(
                MeterFilter.deny(id -> {
                    String name = id.getName();
                    // Исключаем системные метрики, если нужно
                    return name.startsWith("jvm.") ||
                            name.startsWith("system.") ||
                            name.startsWith("process.");
                })
        );

        return registry;
    }

    @Bean
    public MeterFilter commonTagsMeterFilter() {
        return MeterFilter.commonTags(
                io.micrometer.core.instrument.Tags.of(
                        "application", "atc-backend",
                        "namespace", "express",
                        "component", "integration"
                )
        );
    }
}
