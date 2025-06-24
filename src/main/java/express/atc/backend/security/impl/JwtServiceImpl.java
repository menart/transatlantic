package express.atc.backend.security.impl;

import express.atc.backend.db.entity.TokenEntity;
import express.atc.backend.db.repository.TokenRepository;
import express.atc.backend.dto.UserDto;
import express.atc.backend.exception.ApiException;
import express.atc.backend.mapper.UserDetailMapper;
import express.atc.backend.model.TokenModel;
import express.atc.backend.security.UserDetail;
import express.atc.backend.security.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import static express.atc.backend.Constants.TOKEN_NOT_VALID;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {


    @Value("${jwt.secret}")
    private String secret;
    @Value("${jwt.expiration}")
    private Long expirationInSecond;
    @Value("${jwt.refresh}")
    private Long expirationRefreshInMinute;
    private final TokenRepository tokenRepository;
    private final UserDetailMapper userDetailMapper;

    /**
     * Извлечение имени пользователя из токена
     *
     * @param token токен
     * @return имя пользователя
     */
    public String extractPhone(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Генерация токена
     *
     * @param userDetails данные пользователя
     * @return токен
     */
    private String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        if (userDetails instanceof UserDetail customUserDetails) {
            claims.put("id", customUserDetails.getId());
            claims.put("phone", customUserDetails.getPhone());
            claims.put("role", customUserDetails.getRole());
        }
        return generateToken(claims, userDetails);
    }

    /**
     * Генерация и сохранение refresh токена
     *
     * @param phone номер телефона пользователя
     * @return идентификатор сгенерированного refresh токена
     */
    private UUID generateRefresh(String phone) {
        return tokenRepository.save(
                        TokenEntity.builder()
                                .expiredAt(LocalDateTime.now().plusMinutes(expirationRefreshInMinute))
                                .createdAt(LocalDateTime.now())
                                .userPhone(phone)
                                .build())
                .getId();
    }

    /**
     * Проверка токена на валидность
     *
     * @param token       токен
     * @param userDetails данные пользователя
     * @return true, если токен валиден
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String userName = extractPhone(token);
        return (userName.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Проверка принадлежности refresh токена указанному пользователю
     *
     * @param refresh идентификатор refresh токена
     * @param phone   номер телефона пользователя для проверки
     * @return true, если refresh токен существует и принадлежит пользователю
     * @throws ApiException если токен не найден или невалиден (HTTP 401)
     */
    @Override
    public boolean checkPhoneByRefresh(UUID refresh, String phone) {
        return tokenRepository.findById(refresh)
                .map(tokenEntity -> phone.equals(tokenEntity.getUserPhone()))
                .orElseThrow(() -> new ApiException(TOKEN_NOT_VALID, HttpStatus.UNAUTHORIZED));
    }

    /**
     * Удаление refresh токена по идентификатору
     *
     * @param refresh идентификатор refresh токена для удаления
     */
    @Override
    public void removeToken(UUID refresh) {
        tokenRepository.findById(refresh).ifPresent(tokenRepository::delete);
    }

    /**
     * Удаление всех просроченных refresh токенов
     *
     * @return количество удаленных токенов
     */
    @Override
    public int removeExpiredTokens() {
        return tokenRepository.removeExpired(LocalDateTime.now());
    }

    /**
     * Извлечение данных из токена
     *
     * @param token           токен
     * @param claimsResolvers функция извлечения данных
     * @param <T>             тип извлекаемых данных
     * @return извлеченные данные
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers) {
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    /**
     * Генерация JWT токена с дополнительными данными
     *
     * @param extraClaims дополнительные данные для включения в токен
     * @param userDetails данные пользователя
     * @return сгенерированный JWT токен
     */
    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder().setClaims(extraClaims).setSubject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationInSecond * 60 * 24))
                .signWith(getSigningKey()).compact();
    }

    /**
     * Проверка токена на просроченность
     *
     * @param token токен
     * @return true, если токен просрочен
     */
    @Override
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Извлечение даты истечения срока действия токена
     *
     * @param token JWT токен
     * @return дата истечения срока действия
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Извлечение всех данных (claims) из токена
     *
     * @param token JWT токен
     * @return все данные из токена
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Получение ключа для подписи токена
     *
     * @return секретный ключ
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Генерация пары токенов (access + refresh) для пользователя
     *
     * @param user DTO пользователя
     * @return модель сгенерированных токенов
     */
    @Transactional
    @Override
    public TokenModel generateTokens(UserDto user) {
        return new TokenModel(
                generateToken(userDetailMapper.toUserDetail(user)),
                expirationInSecond,
                generateRefresh(user.getPhone()),
                expirationRefreshInMinute
        );
    }
}