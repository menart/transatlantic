package express.atc.backend.integration.cbrf.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import express.atc.backend.integration.cbrf.dto.CurrencyDto;
import express.atc.backend.integration.cbrf.dto.ListCurrencyDto;
import express.atc.backend.integration.cbrf.service.CbrfService;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CbrfServiceImpl implements CbrfService {

    private final WebClient cbrfWebClient;
    @Getter
    private Map<String, CurrencyDto> currencyMap = new HashMap<>();

    @PostConstruct
    public void updateCurrency() {
        var response = cbrfWebClient
                .get()
                .accept(MediaType.APPLICATION_XML)
                .acceptCharset(Charset.defaultCharset())
                .retrieve()
                .bodyToMono(String.class)
                .block();
        try {
            XmlMapper xmlMapper = new XmlMapper();
            xmlMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
            var listCurrency = xmlMapper.readValue(response, ListCurrencyDto.class);
            currencyMap = listCurrency.getCurrencyList().stream()
                    .collect(Collectors.toMap(CurrencyDto::charCode, Function.identity()));
            log.info("{}", currencyMap);
        } catch (JsonProcessingException exception) {
            log.error("{}", (Object) exception.getStackTrace());
        }
    }

}
