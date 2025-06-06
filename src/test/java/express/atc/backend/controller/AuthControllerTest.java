package express.atc.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import express.atc.backend.dto.*;
import express.atc.backend.enums.Language;
import express.atc.backend.enums.UserRole;
import express.atc.backend.exception.AuthSmsException;
import express.atc.backend.mapper.UserDetailMapper;
import express.atc.backend.mapper.UserMapper;
import express.atc.backend.model.AuthResponseModel;
import express.atc.backend.model.TokenModel;
import express.atc.backend.service.AuthService;
import express.atc.backend.service.JwtService;
import express.atc.backend.service.UserService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.UUID;

import static express.atc.backend.Constants.REFRESH_TOKEN;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false) // Отключаем все фильтры
@ContextConfiguration(classes = AuthControllerTest.TestConfig.class)
@Import(ErrorHandlingControllerAdvice.class)
public class AuthControllerTest {

    @Configuration
    @Import(AuthController.class)
    static class TestConfig {
        @Bean
        public RequestInfo requestInfo() {
            return new RequestInfo();
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserMapper userMapper;

    @MockitoBean
    private UserDetailMapper userDetailMapper;

    @Autowired
    private RequestInfo requestInfo; // Внедряем бин RequestInfo

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Создание валидного UserDto для тестов
    private UserDto createValidUserDto() {
        DocumentDto document = new DocumentDto();
        document.setTypeId(21); // Паспорт РФ
        document.setSeries("1234");
        document.setNumber("567890");
        document.setIssueDate(LocalDate.now().minusYears(5));
        document.setExpiredDate(LocalDate.now().plusYears(5));

        return UserDto.builder()
                .id(1L)
                .email("test@mail.ru")
                .phone("79991234567")
                .firstName("Test")
                .lastName("User")
                .surname("Surname")
                .birthday(LocalDate.now().minusYears(30))
                .document(document)
                .inn("123456789012")
                .language(Language.RU)
                .confirmationEmail(true)
                .agree(true)
                .role(UserRole.ROLE_USER)
                .build();
    }

    // Создание валидного TokenModel
    private TokenModel createValidTokenModel() {
        return new TokenModel(
                "access_token",
                30L, // accessTokenExpiresIn в минутах
                UUID.randomUUID(),
                1440L // refreshTokenExpiresIn в минутах
        );
    }

    // Создание UserShortDto
    private UserShortDto createUserShortDto() {
        return new UserShortDto(
                "Test",
                "User",
                "Surname",
                LocalDate.now().minusYears(30),
                "test@mail.ru",
                true
        );
    }

    @Test
    void makeCode_Success() throws Exception {
        AuthSmsDto request = new AuthSmsDto("79991234567");
        Mockito.when(authService.makeCode(anyString(), any())).thenReturn(60);

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post("/api/auth/make")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(req -> {
                    req.setRemoteAddr("192.168.1.1"); // Устанавливаем IP напрямую
                    return req;
                });

        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(content().string("60"));
    }

    @Test
    void validateCode_Success() throws Exception {
        ValidateSmsDto request = new ValidateSmsDto("79991234567", "1234");
        AuthResponseModel responseModel = new AuthResponseModel(
                createValidTokenModel(),
                createValidUserDto()
        );

        Mockito.when(userMapper.toShortDto(any())).thenReturn(createUserShortDto());
        Mockito.when(authService.validateCode(any())).thenReturn(responseModel);

        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        assertNotNull(response.getCookie(REFRESH_TOKEN));
        assertNotNull(response.getCookie("access_token"));
    }

    @Test
    void getSms_WhenEnabled_ReturnsCode() throws Exception {
        Mockito.when(authService.getSms(anyString())).thenReturn("654321");

        mockMvc.perform(MockMvcRequestBuilders.get("/api/auth/sms")
                        .param("phone", "79991234567"))
                .andExpect(status().isOk())
                .andExpect(content().string("654321"));
    }

    @Test
    void auth_Success() throws Exception {
        LoginDto request = new LoginDto("test@mail.ru", "password");
        AuthResponseModel responseModel = new AuthResponseModel(
                createValidTokenModel(),
                createValidUserDto()
        );

        Mockito.when(userMapper.toShortDto(any())).thenReturn(createUserShortDto());
        Mockito.when(authService.login(any())).thenReturn(responseModel);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void logout_RemovesCookies() throws Exception {
        UUID refreshToken = UUID.randomUUID();

        mockMvc.perform(MockMvcRequestBuilders.get("/api/auth/logout")
                        .cookie(new Cookie(REFRESH_TOKEN, refreshToken.toString())))
                .andExpect(status().isOk())
                .andExpect(cookie().maxAge("access_token", 0))
                .andExpect(cookie().maxAge(REFRESH_TOKEN, 0));
    }

    @Test
    void validateCode_InvalidCode_ThrowsException() throws Exception {
        ValidateSmsDto request = new ValidateSmsDto("79991234567", "7777");
        Mockito.when(authService.validateCode(any())).thenThrow(new AuthSmsException("Invalid code"));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/validate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void registration_Success() throws Exception {
        RegistrationDto request = new RegistrationDto(
                "79991234567",
                "test@mail.ru",
                "password",
                "password", // confirmation
                true,       // agree
                "123456"    // code
        );

        AuthResponseModel responseModel = new AuthResponseModel(
                createValidTokenModel(),
                createValidUserDto()
        );

        Mockito.when(userMapper.toShortDto(any())).thenReturn(createUserShortDto());
        Mockito.when(authService.registration(any())).thenReturn(responseModel);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }
}