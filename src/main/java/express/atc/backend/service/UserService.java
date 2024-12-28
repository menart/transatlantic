package express.atc.backend.service;

import express.atc.backend.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Set;

public interface UserService {

    UserDto findOrCreateByPhone(String phone);

    UserDto findUserByPhone(String phone);

    UserDetailsService userDetailsService();

    UserDto updateFullUserInfo(UserDto userInfo);

    Set<String> getBatchUserPhone(int batchSize);
}
