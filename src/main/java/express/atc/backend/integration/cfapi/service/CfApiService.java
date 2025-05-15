package express.atc.backend.integration.cfapi.service;

import express.atc.backend.integration.cfapi.enums.OrderStatus;

public interface CfApiService {
    Boolean changeStatusToCargoflow(String trackingNumber, OrderStatus status);
}
