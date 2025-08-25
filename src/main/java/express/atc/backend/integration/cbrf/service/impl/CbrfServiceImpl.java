package express.atc.backend.integration.cbrf.service.impl;

import express.atc.backend.integration.cbrf.client.CbrfClient;
import express.atc.backend.integration.cbrf.dto.CurrencyDto;
import express.atc.backend.integration.cbrf.dto.ListCurrencyDto;
import express.atc.backend.integration.cbrf.service.CbrfService;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CbrfServiceImpl implements CbrfService {

    private final CbrfClient cbrfClient;

    @Getter
    private Map<String, CurrencyDto> currencyMap = new HashMap<>();

    @PostConstruct
    public void updateCurrency() {
        try {
            ListCurrencyDto listCurrency = cbrfClient.getCurrencyRates(
                    MediaType.APPLICATION_XML_VALUE,
                    Charset.defaultCharset().name()
            );

            currencyMap = listCurrency.getCurrencyList().stream()
                    .collect(Collectors.toMap(CurrencyDto::charCode, Function.identity()));

            log.info("Successfully updated currency rates. Currencies: {}", currencyMap.keySet());

        } catch (Exception exception) {
            log.error("Failed to update currency rates: {}", exception.getMessage(), exception);
        }
    }

    @Override
    public Map<String, CurrencyDto> getCurrencyMap() {
        return currencyMap;
    }
}