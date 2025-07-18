package express.atc.backend.integration.cfapi.dto;

public record CfApiPersonalInfoDto(
        String logisticsOrderCode,
        PersonInfoDto personInfo
) {
}
