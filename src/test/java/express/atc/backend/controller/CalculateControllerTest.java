//package express.atc.backend.controller;
//
//import express.atc.backend.AbstractControllerTest;
//import express.atc.backend.dto.CalculateDto;
//import express.atc.backend.dto.ErrorResponseDto;
//import express.atc.backend.dto.OrdersDto;
//import express.atc.backend.integration.cbrf.dto.CurrencyDto;
//import lombok.SneakyThrows;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.Arguments;
//import org.junit.jupiter.params.provider.MethodSource;
//import org.springframework.http.MediaType;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Stream;
//
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//
//class CalculateControllerTest extends AbstractControllerTest {
//
//    private final String webPath = "/api/calculate/customs-fee";
//    private final String path = "controller/CalculateController/";
//
//    private void mockCbrfCurrency() {
//        Map<String, CurrencyDto> currencyDtoMap = new HashMap<>();
//        currencyDtoMap.put("EUR", new CurrencyDto(
//                "R01239",
//                "978",
//                "EUR",
//                1,
//                "Евро",
//                106.8883,
//                106.8883)
//        );
//        currencyDtoMap.put("USD", new CurrencyDto(
//                "R01235",
//                "840",
//                "USD",
//                1,
//                "Доллар США",
//                98.0562,
//                98.0562)
//        );
//        when(cbrfService.getCurrencyMap())
//                .thenReturn(currencyDtoMap);
//    }
//
//    @SneakyThrows
//    @Test
//    void calcCustomersFeeNotFoundCurrency() {
//        mockCbrfCurrency();
//        var request = new OrdersDto();
//        request.setCurrency("NOT");
//        request.setItems(new ArrayList<>());
//        var response = new ErrorResponseDto("BAD_REQUEST", List.of("Данный тип валюты не найден в ЦБ РФ"));
//        mvc.perform(post(webPath)
//                        .content(objectMapper.writeValueAsString(request))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest())
//                .andExpect(content().json(objectMapper.writeValueAsString(response)));
//    }
//
//    @SneakyThrows
//    @ParameterizedTest
//    @MethodSource("calcParameters")
//    void calcCustomersFeeCalcTest(String test) {
//        mockCbrfCurrency();
//        var request = loadContent(String.format("%s%sRequest.json", path, test), OrdersDto.class);
//        var response = loadContent(String.format("%s%sResponse.json", path, test), CalculateDto.class);
//        mvc.perform(post(webPath)
//                        .content(objectMapper.writeValueAsString(request))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(response == null
//                        ? jsonPath("$").doesNotExist()
//                        : content().json(objectMapper.writeValueAsString(response)));
//    }
//
//    public static Stream<Arguments> calcParameters() {
//        List<String> list = List.of(
//                "NullPay",
//                "PricePay",
//                "WeightPay",
//                "WeightLtPricePay",
//                "PricePayUSD",
//                "WeightPayUSD",
//                "WeightLtPricePayUSD",
//                "WeightGtPricePay",
//                "PricePayRUB"
//        );
//        return list.stream()
//                .map(Arguments::of);
//    }
//}