package express.atc.backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import express.atc.backend.dto.ErrorResponseDto;
import express.atc.backend.dto.RequestInfo;
import express.atc.backend.dto.UserDto;
import express.atc.backend.enums.Language;
import express.atc.backend.exception.ApiException;
import express.atc.backend.helper.AuthHelper;
import express.atc.backend.mapper.UserDetailMapper;
import express.atc.backend.model.TokenModel;
import express.atc.backend.service.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static express.atc.backend.Constants.*;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserService userService;
    private final UserDetailMapper userDetailMapper;
    private final RequestInfo requestInfo;
    private final ObjectMapper objectMapper;
    private final List<String> publicEndpoints;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // Проверяем, публичный ли эндпоинт
        if (isPublicEndpoint(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        // Получаем токен из заголовка
        var accessToken = AuthHelper.extractTokenFromCookie(ACCESS_TOKEN, request);
        UUID refreshToken = null;
        String rawRefreshToken = AuthHelper.extractTokenFromCookie(REFRESH_TOKEN, request);
        try {
            refreshToken = StringUtils.isNotBlank(rawRefreshToken) ? UUID.fromString(rawRefreshToken) : null;
        } catch (IllegalArgumentException e) {
            sendErrorResponse(response, TOKEN_NOT_VALID, HttpStatus.BAD_REQUEST);
        }

        if (StringUtils.isEmpty(accessToken) && StringUtils.isEmpty(rawRefreshToken)) {
            sendErrorResponse(response, NEED_AUTHORIZED, HttpStatus.UNAUTHORIZED);
            return;
        }

        var langHeader = request.getHeader(LANG_HEADER_NAME);
        String phone = null;
        boolean accessTokenExpired = false;

        // Проверяем access токен
        if (StringUtils.isNotEmpty(accessToken)) {
            try {
                phone = jwtService.extractPhone(accessToken);
            } catch (ExpiredJwtException ex) {
                phone = ex.getClaims().getSubject();
                accessTokenExpired = true;
            } catch (Exception ex) {
                // Ошибка валидации токена (неверная подпись и т.д.)
                sendErrorResponse(response, TOKEN_NOT_VALID, HttpStatus.BAD_REQUEST);
            }
        }

        // Обрабатываем случай истечения access токена
        if (accessTokenExpired && refreshToken != null && phone != null) {
            try {
                TokenModel newTokens = refreshTokens(refreshToken, phone);
                AuthHelper.setTokenCookie(response, newTokens, request.isSecure());
                accessToken = newTokens.accessToken(); // Обновляем access токен
                accessTokenExpired = false; // Сбрасываем флаг истечения

                // Обновляем refresh токен для следующей итерации
                refreshToken = newTokens.refreshToken();
            } catch (ApiException ex) {
                AuthHelper.removeTokenCookie(response, request.isSecure());
                if (TOKEN_NOT_VALID.equals(ex.getMessage())) {
                    sendErrorResponse(response, TOKEN_NOT_VALID, HttpStatus.UNAUTHORIZED);
                } else {
                    sendErrorResponse(response, ex.getMessage(), HttpStatus.UNAUTHORIZED);
                }
                return;
            }
        }

        // Аутентификация пользователя
        if (phone != null && !accessTokenExpired && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDto userDto = userService.findUserByPhone(phone);
            UserDetails userDetails = userDetailMapper.toUserDetail(userDto);

            if (jwtService.isTokenValid(accessToken, userDetails)) {
                SecurityContext context = SecurityContextHolder.createEmptyContext();
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                context.setAuthentication(authToken);
                SecurityContextHolder.setContext(context);
                requestInfo.setUser(userDto);
                requestInfo.setLanguage(langHeader == null ? userDto.getLanguage() : Language.getLanguage(langHeader));
            }
        }

        filterChain.doFilter(request, response);
    }

    TokenModel refreshTokens(UUID refresh, String phone) throws ApiException {
        if (StringUtils.isBlank(phone) || !(jwtService.checkPhoneByRefresh(refresh, phone))) {
            throw new ApiException(TOKEN_NOT_VALID, HttpStatus.UNAUTHORIZED);
        }
        var user = Optional.of(userService.findUserByPhone(phone))
                .orElseThrow(() -> new ApiException(TOKEN_NOT_VALID, HttpStatus.UNAUTHORIZED));
        jwtService.removeToken(refresh);
        return jwtService.generateTokens(user);
    }

    public void sendErrorResponse(HttpServletResponse response, String message, HttpStatus status) throws IOException {
        ErrorResponseDto errorResponse = new ErrorResponseDto(
                status.name(),
                List.of(message)
        );

        response.setStatus(status.value());
        response.setContentType(String.valueOf(MediaType.APPLICATION_JSON));
        response.setCharacterEncoding("UTF-8");
        objectMapper.writeValue(response.getWriter(), errorResponse);
    }

    // Проверка, является ли эндпоинт публичным
    public boolean isPublicEndpoint(HttpServletRequest request) {
        AntPathMatcher pathMatcher = new AntPathMatcher();
        String requestURI = request.getRequestURI();

        return publicEndpoints.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, requestURI));
    }
}