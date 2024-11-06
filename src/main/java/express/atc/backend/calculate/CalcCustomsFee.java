package express.atc.backend.calculate;

import express.atc.backend.dto.CalculateDto;
import express.atc.backend.dto.OrdersDto;
import express.atc.backend.exception.BadRequestException;

public interface CalcCustomsFee {

    CalculateDto calculate(OrdersDto ordersDto) throws BadRequestException;
}
