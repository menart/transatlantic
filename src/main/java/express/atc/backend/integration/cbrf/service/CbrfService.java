package express.atc.backend.integration.cbrf.service;

import express.atc.backend.integration.cbrf.dto.CurrencyDto;

import java.util.Map;

public interface CbrfService {

    void updateCurrency();

    Map<String, CurrencyDto> getCurrencyMap();
}
