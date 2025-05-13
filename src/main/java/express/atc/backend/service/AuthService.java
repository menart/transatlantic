package express.atc.backend.service;

import express.atc.backend.dto.*;
import express.atc.backend.exception.AuthSmsException;
import jakarta.validation.Valid;

public interface AuthService {

    int makeCode(String ipAddress, AuthSmsDto authSmsDto) throws AuthSmsException;

    JwtAuthenticationResponse validateCode(ValidateSmsDto validateSms) throws AuthSmsException;

    String getSms(String phone);

    void clearAuthCode();

    JwtAuthenticationResponse login(LoginDto login) throws AuthSmsException;

    UserDto registration(@Valid RegistrationDto registration);
}
