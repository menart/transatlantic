package express.atc.backend.integration.cfapi.service;

import express.atc.backend.dto.ProviderInfoDto;
import express.atc.backend.dto.UserDto;
import express.atc.backend.integration.cfapi.enums.OrderStatus;

public interface CfApiService {
    Boolean sendPersonalInfo(String trackingNumber, UserDto user, ProviderInfoDto provider);
    Boolean changeStatusToCargoflow(String trackingNumber, OrderStatus status);
}