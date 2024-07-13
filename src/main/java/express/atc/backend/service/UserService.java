package express.atc.backend.service;

import express.atc.backend.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService {

    UserDto findOrCreateByPhone(String phone);

    UserDto findUserByPhone(String phone);

    UserDetailsService userDetailsService();
}
