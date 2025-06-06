package express.atc.backend.service;

import express.atc.backend.dto.AuthSmsDto;
import express.atc.backend.dto.LoginDto;
import express.atc.backend.dto.RegistrationDto;
import express.atc.backend.dto.ValidateSmsDto;
import express.atc.backend.exception.AuthSmsException;
import express.atc.backend.model.AuthResponseModel;
import jakarta.validation.Valid;

import java.util.UUID;

public interface AuthService {

    int makeCode(String ipAddress, AuthSmsDto authSmsDto) throws AuthSmsException;

    int checkUserPhone(String ipAddress, AuthSmsDto authSmsDto) throws AuthSmsException;

    AuthResponseModel validateCode(ValidateSmsDto validateSms) throws AuthSmsException;

    String getSms(String phone);

    void clearExpired();

    AuthResponseModel login(LoginDto login) throws AuthSmsException;

    AuthResponseModel registration(@Valid RegistrationDto registration);

    boolean logout(UUID refresh);

}
