package express.atc.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Ответ c токеном доступа")
public record JwtAuthenticationResponse(
        @Schema(description = "Токен доступа", example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTYyMjUwNj...")
        String token,
        @Schema(description = "Токен для обновления токена доступа", example = "58e7669a-ba36-4388-b662-2e0d3a1d56ef")
        @JsonProperty("refresh_token")
        UUID refreshToken,
        Boolean full
) {

    public JwtAuthenticationResponse {
        if (full == null) full = true;
    }
}