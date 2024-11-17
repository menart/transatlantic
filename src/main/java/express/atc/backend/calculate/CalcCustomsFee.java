package express.atc.backend.calculate;

import express.atc.backend.dto.CalculateDto;
import express.atc.backend.dto.OrdersDto;

public interface CalcCustomsFee {

    CalculateDto calculate(OrdersDto ordersDto);
}
