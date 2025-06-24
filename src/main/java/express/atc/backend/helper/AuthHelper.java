package express.atc.backend.helper;

import express.atc.backend.model.TokenModel;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;

import java.time.Duration;

import static express.atc.backend.Constants.ACCESS_TOKEN;
import static express.atc.backend.Constants.REFRESH_TOKEN;

public class AuthHelper {

    private final static String path = "/api";

    // Утилитарный метод для установки refresh токена в куки
    public static void setTokenCookie(HttpServletResponse response, TokenModel tokens, boolean secure) {
        response.addHeader("Set-Cookie",
                setCookie(ACCESS_TOKEN,
                        tokens.accessToken(),
                        Duration.ofMinutes(tokens.refreshTokenExpiresIn()).getSeconds(),
                        secure).toString());

        response.addHeader("Set-Cookie",
                setCookie(REFRESH_TOKEN,
                        tokens.refreshToken().toString(),
                        Duration.ofMinutes(tokens.refreshTokenExpiresIn()).getSeconds(),
                        secure).toString());
    }

    // Утилитарный метод для удаления refresh токена из кук
    public static void removeTokenCookie(HttpServletResponse response, boolean secure) {
        response.addHeader("Set-Cookie", setCookie(ACCESS_TOKEN, "", 0, secure).toString());
        response.addHeader("Set-Cookie", setCookie(REFRESH_TOKEN, "", 0, secure).toString());
    }

    // Извлечение refresh токена из кук
    public static String extractTokenFromCookie(String tokenCookie, HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (tokenCookie.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        //throw new ApiException("Refresh token is missing", HttpStatus.UNAUTHORIZED);
        return null;
    }

    private static ResponseCookie setCookie(String name, String value, long age, boolean secure) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(secure)
                .path(path)
                .sameSite("Strict")
                .maxAge(age)
                .build();
    }
}
