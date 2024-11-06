package express.atc.backend.calculate.impl;

import express.atc.backend.calculate.CalcCustomsFee;
import express.atc.backend.dto.CalculateDto;
import express.atc.backend.dto.OrdersDto;
import express.atc.backend.dto.RateDto;
import express.atc.backend.exception.BadRequestException;
import express.atc.backend.integration.cbrf.dto.CurrencyDto;
import express.atc.backend.integration.cbrf.service.CbrfService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static express.atc.backend.Constants.GRAMS_PER_KG;
import static express.atc.backend.Constants.LOCATE_CURRENCY;

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
        if (weight == null || weight.compareTo(limit) < 0) {
            return BigDecimal.ZERO;
        }
        return weight
                .subtract(limit)
                .divide(new BigDecimal(GRAMS_PER_KG), 0, RoundingMode.CEILING)
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

    private BigDecimal convertToCalcCurrency(String currency, BigDecimal amount) throws BadRequestException {
        return convertCurrency(currency, calcCurrency, amount);
    }

    private BigDecimal convertToLocateCurrency(String currency, BigDecimal amount) throws BadRequestException {
        return convertCurrency(currency, LOCATE_CURRENCY, amount);
    }

    private BigDecimal convertCalcToLocateCurrency(BigDecimal amount) throws BadRequestException {
        return convertToLocateCurrency(calcCurrency, amount);
    }

    private BigDecimal convertCurrency(String sourceCurrency, String targetCurrency, BigDecimal amount) throws BadRequestException {
        if (sourceCurrency.equals(targetCurrency)) {
            return amount;
        }
        BigDecimal sourceRate = getRate(sourceCurrency);
        BigDecimal targetRate = getRate(targetCurrency);
        return amount.multiply(sourceRate).divide(targetRate, 0, RoundingMode.CEILING);
    }

    private BigDecimal getRate(String currency) throws BadRequestException {
        if (currency.equals(LOCATE_CURRENCY)) {
            return BigDecimal.ONE;
        }
        if (!cbrfService.getCurrencyMap().containsKey(currency)) {
            throw new BadRequestException("Данный тип валюты не найден в ЦБ РФ");
        }
        return BigDecimal.valueOf(cbrfService.getCurrencyMap().get(currency).getValueUnitRate());
    }

    private RateDto getRateInfo(String currency) throws BadRequestException {
        if (currency.equals(LOCATE_CURRENCY)) {
            return RateDto.builder()
                    .build();
        }
        if (!cbrfService.getCurrencyMap().containsKey(currency)) {
            throw new BadRequestException("Данный тип валюты не найден в ЦБ РФ");
        }
        CurrencyDto currencyInfo = cbrfService.getCurrencyMap().get(currency);
        return RateDto.builder()
                .name(currencyInfo.getName())
                .code(currencyInfo.getCharCode())
                .rate(currencyInfo.getValueUnitRate())
                .build();
    }


    @Override
    public CalculateDto calculate(OrdersDto ordersDto) throws BadRequestException {
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
                .rates(rateDtoSet.stream().filter(Objects::nonNull).toList())
                .tax(fixedOurTax.longValue());
        if (calcWeight.equals(BigDecimal.ZERO) && calcPrice.equals(BigDecimal.ZERO)) {
            return calculate.build();
        }
        String type = calcWeight.compareTo(calcPrice) > 0
                ? "Расчет по весу"
                : "Расчет по сумме";
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
