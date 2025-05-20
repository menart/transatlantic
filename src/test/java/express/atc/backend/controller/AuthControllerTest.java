//package express.atc.backend.controller;
//
//import express.atc.backend.AbstractControllerTest;
//import express.atc.backend.db.entity.AuthSmsEntity;
//import express.atc.backend.db.repository.AuthSmsRepository;
//import express.atc.backend.dto.AuthSmsDto;
//import express.atc.backend.dto.ErrorResponseDto;
//import express.atc.backend.dto.ValidateSmsDto;
//import express.atc.backend.service.MessageService;
//import lombok.SneakyThrows;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.Arguments;
//import org.junit.jupiter.params.provider.MethodSource;
//import org.mockito.ArgumentCaptor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Stream;
//
//import static express.atc.backend.Constants.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@Slf4j
//class AuthControllerTest extends AbstractControllerTest {
//
//    @Value(value = "${auth.time_hold_sms}")
//    private int TIME_HOLD_SMS;
//    private final String webPath = "/api/auth";
//    private final String makePath = webPath + "/make";
//    private final String validatePath = webPath + "/validate";
//    private final String getPath = webPath + "/sms";
//
//    @MockBean
//    private AuthSmsRepository authSmsRepository;
//    @MockBean
//    private MessageService messageService;
//
//    @Test
//    @SneakyThrows
//    void makeCodeOkTest() {
//        var request = new AuthSmsDto(
//                "79174165380"
//        );
//        when(authSmsRepository.save(any()))
//                .then(invocation -> invocation.getArgument(0));
//        ArgumentCaptor<AuthSmsEntity> actualArgumentCaptor =
//                ArgumentCaptor.forClass(AuthSmsEntity.class);
//        mvc.perform(post(makePath)
//                        .content(objectMapper.writeValueAsString(request))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$").isNumber())
//                .andExpect(jsonPath("$").value(TIME_HOLD_SMS));
//        verify(authSmsRepository, times(1))
//                .save(actualArgumentCaptor.capture());
//        var authSmsEntity = actualArgumentCaptor.getValue();
//        verify(messageService, times(1))
//                .send(request.phone(), String.format(SMS_CODE_MESSAGE, authSmsEntity.getCode()));
//    }
//
//    @SneakyThrows
//    @ParameterizedTest
//    @MethodSource("failMakeCode")
//    void makeCodeFalseTest(String phone, boolean agree, List<String> errorResponse) {
//        var request = new AuthSmsDto(phone);
//        var response = new ErrorResponseDto("Bad Request", errorResponse);
//        when(authSmsRepository.save(any()))
//                .then(invocation -> invocation.getArgument(0));
//        mvc.perform(post(makePath)
//                        .content(objectMapper.writeValueAsString(request))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isBadRequest())
//                .andExpect(content().json(objectMapper.writeValueAsString(response)));
//        verify(authSmsRepository, times(0)).save(any());
//        verify(messageService, times(0)).send(any(), any());
//    }
//
//    @Test
//    @SneakyThrows
//    void makeCodeShortTimeTest() {
//        var request = new AuthSmsDto("79174165380", true);
//        var response = new ErrorResponseDto("Unauthorized", List.of(MESSAGE_SMALL_INTERVAL));
//        when(authSmsRepository.save(any()))
//                .then(invocation -> invocation.getArgument(0));
//        mvc.perform(post(makePath)
//                        .content(objectMapper.writeValueAsString(request))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$").isNumber())
//                .andExpect(jsonPath("$").value(TIME_HOLD_SMS));
//
//        Thread.sleep(TIME_HOLD_SMS - 1);
//        when(authSmsRepository.countByIpaddressAndCreatedAtAfter(any(), any()))
//                .thenReturn(1);
//        mvc.perform(post(webPath + "/make")
//                        .content(objectMapper.writeValueAsString(request))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isUnauthorized())
//                .andExpect(content().json(objectMapper.writeValueAsString(response)));
//        verify(authSmsRepository, times(1)).save(any());
//        verify(messageService, times(1)).send(any(), any());
//    }
//
//    @Test
//    @SneakyThrows
//    void validateCode() {
//        var request = new ValidateSmsDto("79174165380", "1234");
//        when(authSmsRepository
//                .findFirstByPhoneAndCodeAndCreatedAtAfter(
//                        eq(request.phone()), eq(request.code()), notNull()))
//                .thenReturn(Optional.of(new AuthSmsEntity()));
//        mvc.perform(post(validatePath)
//                        .content(objectMapper.writeValueAsString(request))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk());
//    }
//
//    @Test
//    @SneakyThrows
//    void getSms() {
//        String phone = "79174165380";
//        String code = "0123";
//        when(authSmsRepository.findFirstByPhoneOrderByCreatedAtDesc(phone))
//                .thenReturn(Optional.of(AuthSmsEntity.builder()
//                        .code(code)
//                        .build()));
//        mvc.perform(get(getPath)
//                        .queryParam("phone", phone))
//                .andExpect(status().isOk())
//                .andExpect(content().string(code));
//    }
//
//    public static Stream<Arguments> failMakeCode() {
//        return Stream.of(
//                Arguments.of("79174165380", false, List.of(DISAGREE)),
//                Arguments.of("1", true, List.of(PHONE_NOT_VALID)),
//                Arguments.of("1", false, List.of(PHONE_NOT_VALID, DISAGREE))
//        );
//    }
//}