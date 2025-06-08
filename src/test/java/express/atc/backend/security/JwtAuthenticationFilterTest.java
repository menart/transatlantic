package express.atc.backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import express.atc.backend.dto.ErrorResponseDto;
import express.atc.backend.dto.RequestInfo;
import express.atc.backend.dto.UserDto;
import express.atc.backend.enums.Language;
import express.atc.backend.enums.UserRole;
import express.atc.backend.exception.ApiException;
import express.atc.backend.mapper.UserDetailMapper;
import express.atc.backend.model.TokenModel;
import express.atc.backend.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static express.atc.backend.Constants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8_VALUE;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserService userService;

    @Mock
    private UserDetailMapper userDetailMapper;

    @Mock
    private RequestInfo requestInfo;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter filter;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();

        // Инициализация публичных эндпоинтов
        filter = new JwtAuthenticationFilter(
                jwtService,
                userService,
                userDetailMapper,
                requestInfo,
                objectMapper,
                List.of(
                        "/api/auth/make",
                        "/api/auth/validate",
                        "/api/auth/sms",
                        "/api/auth/auth",
                        "/api/auth/registration",
                        "/api/auth/check-phone",
                        "/api/auth/logout"
                )
        );
    }

    @AfterEach
    void tearDown() {
        // Очищаем контекст безопасности после каждого теста
        SecurityContextHolder.clearContext();
    }

    // Создание валидного UserDto
    private UserDto createValidUserDto() {
        return UserDto.builder()
                .id(1L)
                .email("test@mail.ru")
                .phone("79991234567")
                .firstName("Test")
                .lastName("User")
                .surname("Surname")
                .birthday(LocalDate.now().minusYears(30))
                .language(Language.RU)
                .build();
    }

    // Создание валидного TokenModel
    private TokenModel createValidTokenModel() {
        return new TokenModel(
                "new_access_token",
                30L,
                UUID.randomUUID(),
                1440L
        );
    }

    // Создание UserDetails
    private UserDetail createUserDetails() {
        return UserDetail.builder()
                .phone("79991234567")
                .role(UserRole.ROLE_USER)
                .build();
    }

    @Test
    void publicEndpoint_ShouldSkipAuthentication() throws ServletException, IOException {
        request.setRequestURI("/api/auth/make");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void noTokens_ShouldReturnUnauthorized() throws ServletException, IOException {
        request.setRequestURI("/api/secure");

        filter.doFilterInternal(request, response, filterChain);

        assertEquals(401, response.getStatus());
        assertEquals(APPLICATION_JSON_UTF8_VALUE, response.getContentType());
    }

    @Test
    void validAccessToken_ShouldAuthenticate() throws ServletException, IOException {
        // Подготовка
        request.setRequestURI("/api/secure");
        request.setCookies(new Cookie(ACCESS_TOKEN, "valid_token"));
        request.addHeader(LANG_HEADER_NAME, "ru");

        UserDto userDto = createValidUserDto();
        UserDetail userDetails = createUserDetails(); // Используем кастомный UserDetail

        when(jwtService.extractPhone("valid_token")).thenReturn("79991234567");
        when(userService.findUserByPhone("79991234567")).thenReturn(userDto);
        when(userDetailMapper.toUserDetail(userDto)).thenReturn(userDetails);
        when(jwtService.isTokenValid("valid_token", userDetails)).thenReturn(true);

        // Выполнение
        filter.doFilterInternal(request, response, filterChain);

        // Проверка
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals(userDetails, authentication.getPrincipal());
        verify(requestInfo).setUser(userDto);
        verify(requestInfo).setLanguage(Language.RU);
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void expiredAccessTokenWithValidRefresh_ShouldRefreshTokens() throws ServletException, IOException {
        // Подготовка
        request.setRequestURI("/api/secure");

        // Фиксируем refresh token для предсказуемой проверки
        UUID fixedRefreshToken = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        request.setCookies(
                new Cookie(ACCESS_TOKEN, "expired_token"),
                new Cookie(REFRESH_TOKEN, fixedRefreshToken.toString())
        );

        UserDto userDto = createValidUserDto();
        UserDetail userDetails = createUserDetails();

        // Создаем фиксированные токены для проверки
        TokenModel newTokens = new TokenModel(
                "new_access_token",
                30L,
                UUID.fromString("123e4567-e89b-12d3-a456-426614174001"),
                1440L
        );

        // Создаем claims для ExpiredJwtException
        Claims claims = Jwts.claims().setSubject("79991234567").build();
        ExpiredJwtException expiredException = new ExpiredJwtException(null, claims, "Token expired");

        // Access token expired
        when(jwtService.extractPhone("expired_token"))
                .thenThrow(expiredException);

        // Refresh token valid
        when(jwtService.checkPhoneByRefresh(fixedRefreshToken, "79991234567")).thenReturn(true);
        when(userService.findUserByPhone("79991234567")).thenReturn(userDto);
        when(jwtService.generateTokens(userDto)).thenReturn(newTokens);
        when(userDetailMapper.toUserDetail(userDto)).thenReturn(userDetails);
        when(jwtService.isTokenValid(newTokens.accessToken(), userDetails)).thenReturn(true);

        // Выполнение
        filter.doFilterInternal(request, response, filterChain);

        // Проверка заголовков Set-Cookie
        List<String> setCookieHeaders = response.getHeaders("Set-Cookie");
        assertEquals(2, setCookieHeaders.size(), "Should have two Set-Cookie headers");

        // Проверка access token cookie
        String accessCookieHeader = findCookieHeader(setCookieHeaders, ACCESS_TOKEN);
        assertNotNull(accessCookieHeader, "Access token cookie header not found");
        assertTrue(accessCookieHeader.contains("new_access_token"), "Access token value mismatch");
        assertTrue(accessCookieHeader.contains("Path=/api"), "Access token path mismatch");
        assertTrue(accessCookieHeader.contains("HttpOnly"), "Access token should be HttpOnly");

        // Проверка refresh token cookie
        String refreshCookieHeader = findCookieHeader(setCookieHeaders, REFRESH_TOKEN);
        assertNotNull(refreshCookieHeader, "Refresh token cookie header not found");
        assertTrue(refreshCookieHeader.contains("123e4567-e89b-12d3-a456-426614174001"), "Refresh token value mismatch");
        assertTrue(refreshCookieHeader.contains("Path=/api"), "Refresh token path mismatch");
        assertTrue(refreshCookieHeader.contains("HttpOnly"), "Refresh token should be HttpOnly");

        // Проверка аутентификации
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        verify(filterChain).doFilter(request, response);
    }

    // Вспомогательный метод для поиска заголовка по имени куки
    private String findCookieHeader(List<String> headers, String cookieName) {
        return headers.stream()
                .filter(header -> header.startsWith(cookieName + "="))
                .findFirst()
                .orElse(null);
    }


    @Test
    void invalidAccessToken_ShouldReturnBadRequest() throws ServletException, IOException {
        // Подготовка
        request.setRequestURI("/api/secure");
        request.setCookies(new Cookie(ACCESS_TOKEN, "invalid_token"));

        when(jwtService.extractPhone("invalid_token"))
                .thenThrow(new MalformedJwtException("Invalid token"));

        // Выполнение
        filter.doFilterInternal(request, response, filterChain);

        // Проверка
        assertEquals(400, response.getStatus());
        assertEquals(APPLICATION_JSON_UTF8_VALUE, response.getContentType());
    }

    @Test
    void expiredAccessTokenWithInvalidRefresh_ShouldReturnUnauthorized() throws ServletException, IOException {
        // Подготовка
        request.setRequestURI("/api/secure");

        UUID invalidRefreshToken = UUID.randomUUID();
        request.setCookies(
                new Cookie(ACCESS_TOKEN, "expired_token"),
                new Cookie(REFRESH_TOKEN, invalidRefreshToken.toString())
        );

        // Создаем claims для ExpiredJwtException
        Claims claims = Jwts.claims().setSubject("79991234567").build();
        ExpiredJwtException expiredException = new ExpiredJwtException(null, claims, "Token expired");

        when(jwtService.extractPhone("expired_token"))
                .thenThrow(expiredException);

        when(jwtService.checkPhoneByRefresh(invalidRefreshToken, "79991234567")).thenReturn(false);

        // Выполнение
        filter.doFilterInternal(request, response, filterChain);

        // Проверка статуса ответа
        assertEquals(401, response.getStatus());
        assertEquals(APPLICATION_JSON_UTF8_VALUE, response.getContentType());

        // Проверка заголовков Set-Cookie
        List<String> setCookieHeaders = response.getHeaders("Set-Cookie");
        assertEquals(2, setCookieHeaders.size(), "Should have two Set-Cookie headers");

        // Проверка для ACCESS_TOKEN
        String accessCookieHeader = setCookieHeaders.get(0);
        assertTrue(accessCookieHeader.contains(ACCESS_TOKEN + "="));
        assertTrue(accessCookieHeader.contains("Max-Age=0"));
        assertTrue(accessCookieHeader.contains("Path=/api"));
        assertTrue(accessCookieHeader.contains("HttpOnly"));

        // Проверка для REFRESH_TOKEN
        String refreshCookieHeader = setCookieHeaders.get(1);
        assertTrue(refreshCookieHeader.contains(REFRESH_TOKEN + "="));
        assertTrue(refreshCookieHeader.contains("Max-Age=0"));
        assertTrue(refreshCookieHeader.contains("Path=/api"));
        assertTrue(refreshCookieHeader.contains("HttpOnly"));
    }

    @Test
    void invalidRefreshTokenFormat_ShouldReturnBadRequest() throws ServletException, IOException {
        // Подготовка
        request.setRequestURI("/api/secure");
        request.setCookies(new Cookie(REFRESH_TOKEN, "not_a_uuid"));

        // Выполнение
        filter.doFilterInternal(request, response, filterChain);

        // Проверка через ArgumentCaptor
        ArgumentCaptor<ErrorResponseDto> captor = ArgumentCaptor.forClass(ErrorResponseDto.class);
        verify(objectMapper).writeValue(eq(response.getWriter()), captor.capture());

        ErrorResponseDto errorResponse = captor.getValue();
        assertEquals(HttpStatus.BAD_REQUEST.name(), errorResponse.status());
        assertTrue(errorResponse.messages().contains(TOKEN_NOT_VALID));
    }

    @Test
    void tokenRefreshFailure_ShouldHandleApiException() throws ServletException, IOException {
        // Создаем mock claims
        Claims claims = mock(Claims.class);
        when(claims.getSubject()).thenReturn("79991234567");

        ExpiredJwtException expiredException = new ExpiredJwtException(null, claims, "Token expired");

        // Подготовка
        UUID refreshToken = UUID.randomUUID();
        request.setRequestURI("/api/secure");
        request.setCookies(
                new Cookie(ACCESS_TOKEN, "expired_token"),
                new Cookie(REFRESH_TOKEN, refreshToken.toString())
        );

        // Access token expired
        when(jwtService.extractPhone("expired_token"))
                .thenThrow(expiredException);

        // Refresh token valid but refresh fails
        when(jwtService.checkPhoneByRefresh(refreshToken, "79991234567")).thenReturn(true);
        when(userService.findUserByPhone("79991234567"))
                .thenThrow(new ApiException("User not found", HttpStatus.NOT_FOUND));

        // Выполнение
        filter.doFilterInternal(request, response, filterChain);

        // Проверка
        assertEquals(401, response.getStatus());
        assertEquals(APPLICATION_JSON_UTF8_VALUE, response.getContentType());
    }

    @Test
    void validTokenButUserNotFound_ShouldNotAuthenticate() throws ServletException, IOException {
        // Подготовка
        request.setRequestURI("/api/secure");
        request.setCookies(new Cookie(ACCESS_TOKEN, "valid_token"));

        when(jwtService.extractPhone("valid_token")).thenReturn("79991234567");
        when(userService.findUserByPhone("79991234567")).thenReturn(null);

        // Выполнение
        filter.doFilterInternal(request, response, filterChain);

        // Проверка
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void isPublicEndpoint_WithMatchingPattern_ShouldReturnTrue() {
        request.setRequestURI("/api/auth/make");
        assertTrue(filter.isPublicEndpoint(request));
    }

    @Test
    void isPublicEndpoint_WithNonMatchingPattern_ShouldReturnFalse() {
        request.setRequestURI("/api/secure/data");
        assertFalse(filter.isPublicEndpoint(request));
    }

    @Test
    void refreshTokens_ValidRequest_ShouldReturnNewTokens() throws ApiException {
        // Подготовка
        UUID refreshToken = UUID.randomUUID();
        String phone = "79991234567";
        UserDto userDto = createValidUserDto();
        TokenModel expectedTokens = createValidTokenModel();

        when(jwtService.checkPhoneByRefresh(refreshToken, phone)).thenReturn(true);
        when(userService.findUserByPhone(phone)).thenReturn(userDto);
        when(jwtService.generateTokens(userDto)).thenReturn(expectedTokens);

        // Выполнение
        TokenModel actualTokens = filter.refreshTokens(refreshToken, phone);

        // Проверка
        assertEquals(expectedTokens, actualTokens);
        verify(jwtService).removeToken(refreshToken);
    }

    @Test
    void refreshTokens_InvalidPhone_ShouldThrowException() {
        // Подготовка
        UUID refreshToken = UUID.randomUUID();
        String phone = "invalid_phone";

        when(jwtService.checkPhoneByRefresh(refreshToken, phone)).thenReturn(false);

        // Выполнение и проверка
        ApiException exception = assertThrows(ApiException.class, () -> {
            filter.refreshTokens(refreshToken, phone);
        });

        assertEquals(TOKEN_NOT_VALID, exception.getMessage());
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
    }

    @Test
    void sendErrorResponse_ShouldSetCorrectResponse() throws IOException {
        // Подготовка
        HttpStatus status = HttpStatus.BAD_REQUEST;
        String message = "Test error message";
        ErrorResponseDto expectedResponse = new ErrorResponseDto(
                status.name(),
                List.of(message)
        );

        // Захватываем аргументы, переданные в objectMapper.writeValue()
        ArgumentCaptor<Writer> writerCaptor = ArgumentCaptor.forClass(Writer.class);
        ArgumentCaptor<ErrorResponseDto> dtoCaptor = ArgumentCaptor.forClass(ErrorResponseDto.class);

        // Выполнение
        filter.sendErrorResponse(response, message, status);

        // Проверка
        assertEquals(status.value(), response.getStatus());
        assertEquals(APPLICATION_JSON_UTF8_VALUE, response.getContentType());

        // Проверяем вызов writeValue с правильными параметрами
        verify(objectMapper).writeValue(writerCaptor.capture(), dtoCaptor.capture());

        ErrorResponseDto actualDto = dtoCaptor.getValue();
        assertEquals(expectedResponse.status(), actualDto.status());
        assertEquals(expectedResponse.messages(), actualDto.messages());

        // Дополнительная проверка: записанный writer должен быть из response
        assertTrue(writerCaptor.getValue() instanceof PrintWriter);
    }
}