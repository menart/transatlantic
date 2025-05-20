package express.atc.backend.service;

import express.atc.backend.dto.*;
import express.atc.backend.enums.Language;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Set;

public interface UserService {

    UserDto findOrCreateByPhone(String phone);

    UserDto findUserByPhone(String phone);

    UserDetailsService userDetailsService();

    UserDto updateFullUserInfo(UserDto userInfo);

    Set<String> getBatchUserPhone(int batchSize);

    LanguageDto getLanguage(String userPhone);

    LanguageDto setLanguage(String userPhone, Language language);

    UserDto authenticate(LoginDto login);

    UserDto changePassword(String userPhone, ChangePasswordDto changePassword);

    UserDto registrationUser(RegistrationDto registration);

    Boolean dropUser(String userPhone);
}
