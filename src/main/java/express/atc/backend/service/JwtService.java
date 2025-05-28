package express.atc.backend.service;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

public interface JwtService {
    String extractPhone(String token);
    String generateToken(UserDetails userDetails);
    UUID generateRefresh(String phone);
    boolean isTokenValid(String token, UserDetails userDetails);
    String getPhoneByRefresh(UUID refresh);
    void removeToken(UUID refresh);
    int removeExpiredTokens();
}
