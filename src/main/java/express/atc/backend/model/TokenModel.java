package express.atc.backend.model;

import java.util.UUID;

public record TokenModel(
        String accessToken,
        Long accessTokenExpiresIn,
        UUID refreshToken,
        Long refreshTokenExpiresIn
) {
}
