package express.atc.backend.service;

import express.atc.backend.dto.*;
import express.atc.backend.exception.AuthSmsException;
import jakarta.validation.Valid;

import java.util.UUID;

public interface AuthService {

    int makeCode(String ipAddress, AuthSmsDto authSmsDto) throws AuthSmsException;
    int checkUserPhone(String ipAddress, AuthSmsDto authSmsDto) throws AuthSmsException;

    JwtAuthenticationResponse validateCode(ValidateSmsDto validateSms) throws AuthSmsException;

    String getSms(String phone);

    void clearExpired();

    JwtAuthenticationResponse login(LoginDto login) throws AuthSmsException;

    JwtAuthenticationResponse registration(@Valid RegistrationDto registration);

    JwtAuthenticationResponse refresh(UUID refresh);

    boolean logout(UUID refresh);

}
