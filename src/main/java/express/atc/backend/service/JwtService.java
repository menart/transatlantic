package express.atc.backend.service;

import express.atc.backend.dto.UserDto;
import express.atc.backend.model.TokenModel;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

public interface JwtService {
    String extractPhone(String token);
    boolean isTokenValid(String token, UserDetails userDetails);
    boolean isTokenExpired(String token);
    boolean checkPhoneByRefresh(UUID refresh, String phone);
    void removeToken(UUID refresh);
    int removeExpiredTokens();
    TokenModel generateTokens(UserDto user);
}
