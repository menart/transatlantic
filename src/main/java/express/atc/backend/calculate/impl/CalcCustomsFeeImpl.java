package express.atc.backend.calculate.impl;

import express.atc.backend.calculate.CalcCustomsFee;
import express.atc.backend.dto.CalculateDto;
import express.atc.backend.dto.OrdersDto;
import express.atc.backend.exception.ApiException;
import express.atc.backend.integration.cbrf.service.CbrfService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

import static express.atc.backend.Constants.LOCATE_CURRENCY;

@Component
@RequiredArgsConstructor
public class CalcCustomsFeeImpl implements CalcCustomsFee {

    @Value("${customs-fee.currency}")
    private String calcCurrency;
    @Value("${customs-fee.limit-price}")
    private BigInteger limitPrice;
    @Value("${customs-fee.limit-weight}")
    private BigInteger limitWeight;
    @Value("${customs-fee.fixed-our-fee}")
    private BigInteger fixedOurFee;
    @Value("${customs-fee.percent-amends}")
    private BigDecimal percentAmends;
    @Value("${customs-fee.fee-price-percent}")
    private BigDecimal feePricePercent;
    @Value("${customs-fee.fee-weight-by-kg}")
    private BigDecimal feeWeightByKg;

    private final CbrfService cbrfService;

    private BigDecimal calcByWeight(BigDecimal weight) {
        var limit = new BigDecimal(limitWeight);
        if (weight == null || weight.compareTo(limit) < 0) {
            return BigDecimal.ZERO;
        }
        return weight
                .subtract(limit)
                .setScale(-2, RoundingMode.CEILING)
                .multiply(feeWeightByKg);
    }

    private BigDecimal calcByPrice(BigDecimal price) {
        var limit = new BigDecimal(limitPrice);
        if (price == null || price.compareTo(limit) < 0) {
            return BigDecimal.ZERO;
        }
        return price
                .subtract(limit)
                .multiply(feePricePercent)
                .divide(new BigDecimal(100), 0, RoundingMode.CEILING);
    }

    private BigDecimal convertToCalcCurrency(String currency, BigDecimal amount) throws ApiException {
        return convertCurrency(currency, calcCurrency, amount);
    }

    private BigDecimal convertToLocateCurrency(String currency, BigDecimal amount) throws ApiException {
        return convertCurrency(currency, LOCATE_CURRENCY, amount);
    }

    private BigDecimal convertCalcToLocateCurrency(BigDecimal amount) throws ApiException {
        return convertToLocateCurrency(calcCurrency, amount);
    }

    private BigDecimal convertCurrency(String sourceCurrency, String targetCurrency, BigDecimal amount) throws ApiException {
        if (sourceCurrency.equals(targetCurrency)) {
            return amount;
        }
        BigDecimal sourceRate = getRate(sourceCurrency);
        BigDecimal targetRate = getRate(targetCurrency);
        return amount.multiply(sourceRate).divide(targetRate, 0, RoundingMode.CEILING);
    }

    private BigDecimal getRate(String currency) throws ApiException {
        if (currency.equals(LOCATE_CURRENCY)) {
            return BigDecimal.ONE;
        }
        if (!cbrfService.getCurrencyMap().containsKey(currency)) {
            throw new ApiException("Данный тип валюты не найден в ЦБ РФ");
        }
        return BigDecimal.valueOf(cbrfService.getCurrencyMap().get(currency).getValueUnitRate());
    }

    @Override
    public CalculateDto calculate(OrdersDto ordersDto) throws ApiException {
        var calcWeight = calcByWeight(new BigDecimal(ordersDto.getWeight()));
        var calcPrice = calcByPrice(
                convertToCalcCurrency(ordersDto.getCurrency(),
                        new BigDecimal(ordersDto.getPrice())
                )
        );
        if (calcWeight.equals(BigDecimal.ZERO) && calcPrice.equals(BigDecimal.ZERO)) {
            return null;
        }
        String type = calcWeight.compareTo(calcPrice) > 0
                ? "Расчет по весу"
                : "Расчет по сумме";
        BigDecimal fee =
                convertCalcToLocateCurrency(
                        calcWeight.compareTo(calcPrice) > 0 ? calcWeight : calcPrice)
                        .add(new BigDecimal(fixedOurFee))
                        .divide(new BigDecimal(100)
                                        .subtract(percentAmends)
                                        .divide(new BigDecimal(100), 0, RoundingMode.CEILING),
                                0, RoundingMode.CEILING);
        return CalculateDto.builder()
                .type(type)
                .fee(fee.toBigInteger().longValue())
                .build();
    }
}
