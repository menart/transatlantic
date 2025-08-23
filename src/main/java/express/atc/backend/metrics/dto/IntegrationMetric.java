package express.atc.backend.metrics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IntegrationMetric {
    private String integrationName;
    private String operationName;
    private long executionTimeMs;
    private boolean success;
    private String errorMessage;
    private String statusCode;
    private long timestamp;

    public IntegrationMetric(String integrationName, String operationName) {
        this.integrationName = integrationName;
        this.operationName = operationName;
        this.timestamp = System.currentTimeMillis();
    }
}