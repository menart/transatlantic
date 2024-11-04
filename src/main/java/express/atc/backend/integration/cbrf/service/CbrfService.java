package express.atc.backend.integration.cbrf.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import express.atc.backend.integration.cbrf.dto.ListCurrencyDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.nio.charset.Charset;

@Service
@RequiredArgsConstructor
@Slf4j
public class CbrfService {

    private final WebClient cbrfWebClient;

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
            log.info("{}", listCurrency);
        } catch (JsonProcessingException exception) {
            log.error("{}", exception.getStackTrace());
        }
    }
}
