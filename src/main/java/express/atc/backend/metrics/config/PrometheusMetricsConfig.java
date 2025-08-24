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
