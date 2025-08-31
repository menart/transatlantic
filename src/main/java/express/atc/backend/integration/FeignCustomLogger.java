package express.atc.backend.integration;

import feign.Logger;
import feign.Request;
import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class FeignCustomLogger extends Logger {

    @Override
    protected void log(String configKey, String format, Object... args) {
        // Используем debug уровень для обычных логов Feign
        if (log.isDebugEnabled()) {
            log.debug(String.format(methodTag(configKey) + format, args));
        }
    }

    @Override
    protected void logRequest(String configKey, Level logLevel, Request request) {
        if (log.isInfoEnabled()) {
            log.info("=== FEIGN REQUEST ===");
            log.info("Config: {}", configKey);
            log.info("URL: {} {}", request.httpMethod().name(), request.url());
            log.info("Headers:");
            request.headers().forEach((key, values) ->
                    values.forEach(value ->
                            log.info("  {}: {}", key, value)
                    )
            );

            if (request.body() != null && request.length() > 0) {
                log.info("Body: {}", new String(request.body(), StandardCharsets.UTF_8));
            }
            log.info("=====================");
        }
    }

    @Override
    protected Response logAndRebufferResponse(String configKey, Level logLevel,
                                              Response response, long elapsedTime) throws IOException {
        if (log.isInfoEnabled()) {
            log.info("=== FEIGN RESPONSE ===");
            log.info("Config: {}", configKey);
            log.info("Status: {} ({} ms)", response.status(), elapsedTime);
            log.info("Headers:");
            response.headers().forEach((key, values) ->
                    values.forEach(value ->
                            log.info("  {}: {}", key, value)
                    )
            );

            if (response.body() != null) {
                byte[] bodyData = toByteArray(response.body().asInputStream());
                log.info("Body: {}", new String(bodyData, StandardCharsets.UTF_8));
                log.info("=====================");
                return response.toBuilder().body(bodyData).build();
            }
            log.info("=====================");
        }
        return response;
    }

    private byte[] toByteArray(java.io.InputStream input) throws IOException {
        try (java.io.ByteArrayOutputStream output = new java.io.ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = input.read(buffer)) != -1) {
                output.write(buffer, 0, length);
            }
            return output.toByteArray();
        }
    }
}