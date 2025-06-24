package express.atc.backend.model;

import express.atc.backend.dto.UserDto;

public record AuthResponseModel(
        TokenModel tokens,
        UserDto user
) {
}
