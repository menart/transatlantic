package express.atc.backend.calculate.impl;

import express.atc.backend.calculate.CalcCustomsFee;
import express.atc.backend.dto.CalculateDto;
import express.atc.backend.dto.OrdersDto;
import express.atc.backend.dto.RateDto;
import express.atc.backend.exception.ApiException;
import express.atc.backend.integration.cbrf.dto.CurrencyDto;
import express.atc.backend.integration.cbrf.service.CbrfService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static express.atc.backend.Constants.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class CalcCustomsFeeImpl implements CalcCustomsFee {

    @Value("${customs-fee.currency}")
    private String calcCurrency;
    @Value("${customs-fee.limit-price}")
    private BigInteger limitPrice;
    @Value("${customs-fee.limit-weight}")
    private BigInteger limitWeight;
    @Value("${customs-fee.fixed-our-tax}")
    private BigDecimal fixedOurTax;
    @Value("${customs-fee.percent-amends}")
    private BigDecimal percentAmends;
    @Value("${customs-fee.fee-price-percent}")
    private BigDecimal feePricePercent;
    @Value("${customs-fee.fee-weight-by-kg}")
    private BigDecimal feeWeightByKg;

    private final CbrfService cbrfService;

    private BigDecimal calcByWeight(BigDecimal weight) {
        var limit = new BigDecimal(limitWeight);
        if (weight.compareTo(limit) < 0) {
            return BigDecimal.ZERO;
        }
        return weight
                .subtract(limit)
                .divide(new BigDecimal(GRAMS_PER_KG), 0, RoundingMode.CEILING)
                .multiply(feeWeightByKg);
    }

    private BigDecimal calcByPrice(BigDecimal price) {
        var limit = new BigDecimal(limitPrice);
        if (price.compareTo(limit) < 0) {
            return BigDecimal.ZERO;
        }
        return price
                .subtract(limit)
                .multiply(feePricePercent)
                .divide(new BigDecimal(100), 0, RoundingMode.CEILING);
    }

    private BigDecimal convertToCalcCurrency(String currency, BigDecimal amount) {
        return convertCurrency(currency, calcCurrency, amount);
    }

    private BigDecimal convertToLocateCurrency(String currency, BigDecimal amount) {
        return convertCurrency(currency, LOCATE_CURRENCY, amount);
    }

    private BigDecimal convertCalcToLocateCurrency(BigDecimal amount) {
        return convertToLocateCurrency(calcCurrency, amount);
    }

    private BigDecimal convertCurrency(String sourceCurrency, String targetCurrency, BigDecimal amount) {
        if (sourceCurrency.equals(targetCurrency)) {
            return amount;
        }
        BigDecimal sourceRate = getRate(sourceCurrency);
        BigDecimal targetRate = getRate(targetCurrency);
        return amount.multiply(sourceRate).divide(targetRate, 0, RoundingMode.CEILING);
    }

    private BigDecimal getRate(String currency) {
        if (currency.equals(LOCATE_CURRENCY)) {
            return BigDecimal.ONE;
        }
        return BigDecimal.valueOf(cbrfService.getCurrencyMap().get(currency).valueUnitRate());
    }

    private RateDto getRateInfo(String currency) {
        if (currency.equals(LOCATE_CURRENCY)) {
            return RateDto.builder()
                    .build();
        }
        CurrencyDto currencyInfo = cbrfService.getCurrencyMap().get(currency);
        return RateDto.builder()
                .name(currencyInfo.name())
                .code(currencyInfo.charCode())
                .rate(currencyInfo.valueUnitRate())
                .build();
    }


    @Override
    public CalculateDto calculate(OrdersDto ordersDto){
        if (!LOCATE_CURRENCY.equals(ordersDto.getCurrency())
                && !cbrfService.getCurrencyMap().containsKey(ordersDto.getCurrency())) {
            throw new ApiException("Данный тип валюты не найден в ЦБ РФ", HttpStatus.BAD_REQUEST);
        }
        var calcWeight = calcByWeight(new BigDecimal(ordersDto.getWeight()));
        log.info("weigth: {}", calcWeight);
        var calcPrice = calcByPrice(
                convertToCalcCurrency(ordersDto.getCurrency(),
                        new BigDecimal(ordersDto.getPrice())
                )
        );
        log.info("price: {}", calcPrice);

        Set<RateDto> rateDtoSet = new HashSet<>();
        rateDtoSet.add(getRateInfo(calcCurrency));
        rateDtoSet.add(getRateInfo(ordersDto.getCurrency()));
        var calculate = CalculateDto.builder()
                .rates(rateDtoSet.stream()
                        .filter(Objects::nonNull)
                        .filter(rate -> rate.getCode() != null && !rate.getCode().equals(LOCATE_CURRENCY))
                        .toList())
                .tax(fixedOurTax.longValue());
        if (calcWeight.equals(BigDecimal.ZERO) && calcPrice.equals(BigDecimal.ZERO)) {
            return null;
        }
        String type = calcWeight.compareTo(calcPrice) > 0
                ? CALC_WEIGHT
                : CALC_AMOUNT;
        BigDecimal fee =
                convertCalcToLocateCurrency(
                        calcWeight.compareTo(calcPrice) > 0 ? calcWeight : calcPrice)
                        .setScale(0, RoundingMode.CEILING);
        BigDecimal compensation = fee
                .add(fixedOurTax)
                .multiply(percentAmends
                        .divide(new BigDecimal(100), 2, RoundingMode.CEILING));
        BigDecimal paid = fee
                .add(fixedOurTax)
                .add(compensation);
        return calculate
                .paid(paid.longValue())
                .type(type)
                .compensation(compensation.longValue())
                .fee(fee.toBigInteger().longValue())
                .build();
    }
}
