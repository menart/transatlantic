//package express.atc.backend.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import express.atc.backend.calculate.CalcCustomsFee;
//import express.atc.backend.dto.CalculateDto;
//import express.atc.backend.dto.OrderDto;
//import express.atc.backend.dto.OrdersDto;
//import express.atc.backend.model.MoneyModel;
//import express.atc.backend.security.JwtAuthenticationFilter;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.context.annotation.FilterType;
//import org.springframework.http.MediaType;
//import org.springframework.security.config.annotation.web.WebSecurityConfigurer;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//// Фокусируемся только на CalculateController, отключаем безопасность
//@WebMvcTest(
//        controllers = CalculateController.class,
//        includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = CalcCustomsFee.class),
//        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfigurer.class)
//)
//class CalculateControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @MockBean
//    private CalcCustomsFee calcCustomsFee;
//
//    @MockBean
//    private JwtAuthenticationFilter jwtAuthenticationFilter;
//
//    @Test
//    void validRequest_returnsCalculateDto() throws Exception {
//        OrdersDto request = new OrdersDto(
//                new MoneyModel(10000L, "RUB"),
//                "RUB",
//                1000,
//                List.of(new OrderDto())
//        );
//
//        CalculateDto response = new CalculateDto();
//        response.setFee(1000L);
//
//        when(calcCustomsFee.calculate(any(OrdersDto.class))).thenReturn(response);
//
//        mockMvc.perform(post("/api/calculate/customs-fee")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.fee").value(1000));
//    }
//
//    @Test
//    void emptyItems_returnsBadRequest() throws Exception {
//        OrdersDto request = new OrdersDto(
//                new MoneyModel(10000L, "RUB"),
//                "RUB",
//                1000,
//                List.of() // Пустой список товаров
//        );
//
//        mockMvc.perform(post("/api/calculate/customs-fee")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.messages[0]").value("Список товаров не может быть пустым"));
//    }
//
//    @Test
//    void negativeWeight_returnsBadRequest() throws Exception {
//        OrdersDto request = new OrdersDto(
//                new MoneyModel(10000L, "RUB"),
//                "RUB",
//                -100, // Отрицательный вес
//                List.of(new OrderDto())
//        );
//
//        mockMvc.perform(post("/api/calculate/customs-fee")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.messages[0]").value("Вес должен быть положительным"));
//    }
//
//    @Test
//    void invalidCurrency_returnsBadRequest() throws Exception {
//        OrdersDto request = new OrdersDto(
//                new MoneyModel(10000L, "RUB"),
//                "XYZ", // Невалидная валюта
//                1000,
//                List.of(new OrderDto())
//        );
//
//        mockMvc.perform(post("/api/calculate/customs-fee")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.messages[0]").value("Недопустимая валюта"));
//    }
//
//    @Test
//    void serviceThrowsException_returnsInternalError() throws Exception {
//        OrdersDto request = new OrdersDto(
//                new MoneyModel(10000L, "RUB"),
//                "RUB",
//                1000,
//                List.of(new OrderDto())
//        );
//
//        when(calcCustomsFee.calculate(any(OrdersDto.class)))
//                .thenThrow(new RuntimeException("Ошибка расчета"));
//
//        mockMvc.perform(post("/api/calculate/customs-fee")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isInternalServerError())
//                .andExpect(jsonPath("$.messages[0]").value("Ошибка расчета"));
//    }
//}