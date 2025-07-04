package express.atc.backend.dto;

public record TrackingAdminDto(
        TrackingDto dto,
        UserDto user,
        String userPhone
) {
}
