package express.atc.backend.service;

import express.atc.backend.dto.*;
import express.atc.backend.exception.AuthSmsException;
import express.atc.backend.model.TokenModel;
import jakarta.validation.Valid;

import java.util.UUID;

public interface AuthService {

    int makeCode(String ipAddress, AuthSmsDto authSmsDto) throws AuthSmsException;
    int checkUserPhone(String ipAddress, AuthSmsDto authSmsDto) throws AuthSmsException;

    TokenModel validateCode(ValidateSmsDto validateSms) throws AuthSmsException;

    String getSms(String phone);

    void clearExpired();

    TokenModel login(LoginDto login) throws AuthSmsException;

    TokenModel registration(@Valid RegistrationDto registration);

    boolean logout(UUID refresh);

}
