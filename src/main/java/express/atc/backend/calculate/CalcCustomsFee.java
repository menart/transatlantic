package express.atc.backend.calculate;

import express.atc.backend.dto.CalculateDto;
import express.atc.backend.dto.OrdersDto;
import express.atc.backend.exception.ApiException;

public interface CalcCustomsFee {

    CalculateDto calculate(OrdersDto ordersDto) throws ApiException;
}
