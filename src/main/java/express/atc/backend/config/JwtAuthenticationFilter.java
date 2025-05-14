package express.atc.backend.config;

import express.atc.backend.dto.RequestInfo;
import express.atc.backend.dto.UserDto;
import express.atc.backend.enums.Language;
import express.atc.backend.mapper.UserDetailMapper;
import express.atc.backend.service.JwtService;
import express.atc.backend.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static express.atc.backend.Constants.*;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserService userService;
    private final UserDetailMapper userDetailMapper;
    private final RequestInfo requestInfo;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // Получаем токен из заголовка
        var authHeader = request.getHeader(AUTH_HEADER_NAME);
        var langHeader = request.getHeader(LANG_HEADER_NAME);
        if (StringUtils.isEmpty(authHeader) || !StringUtils.startsWith(authHeader, BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Обрезаем префикс и получаем имя пользователя из токена
        var jwt = authHeader.substring(BEARER_PREFIX.length());
        var phone = jwtService.extractPhone(jwt);

        if (StringUtils.isNotEmpty(phone) && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDto userDto = userService.findUserByPhone(phone);
            UserDetails userDetails = userDetailMapper.toUserDetail(userDto);

            // Если токен валиден, то аутентифицируем пользователя
            if (jwtService.isTokenValid(jwt, userDetails)) {
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
}