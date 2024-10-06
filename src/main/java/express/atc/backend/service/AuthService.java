package express.atc.backend.service;

import express.atc.backend.dto.AuthSmsDto;
import express.atc.backend.dto.JwtAuthenticationResponse;
import express.atc.backend.dto.ValidateSmsDto;
import express.atc.backend.exception.AuthSmsException;

public interface AuthService {

    int makeCode(String ipAddress, AuthSmsDto authSmsDto) throws AuthSmsException;

    JwtAuthenticationResponse validateCode(ValidateSmsDto validateSms) throws AuthSmsException;

    String getSms(String phone);
}
