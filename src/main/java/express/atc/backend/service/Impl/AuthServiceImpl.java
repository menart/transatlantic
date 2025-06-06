package express.atc.backend.service.Impl;

import express.atc.backend.db.entity.AuthSmsEntity;
import express.atc.backend.db.repository.AuthSmsRepository;
import express.atc.backend.dto.AuthSmsDto;
import express.atc.backend.dto.LoginDto;
import express.atc.backend.dto.RegistrationDto;
import express.atc.backend.dto.ValidateSmsDto;
import express.atc.backend.exception.ApiException;
import express.atc.backend.exception.AuthSmsException;
import express.atc.backend.mapper.UserDetailMapper;
import express.atc.backend.model.TokenModel;
import express.atc.backend.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.LocalDateTime;
import java.util.UUID;

import static express.atc.backend.Constants.*;

@Service
@CrossOrigin
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthSmsRepository authSmsRepository;
    private final UserService userService;
    private final UserDetailMapper userDetailMapper;
    private final MessageService messageService;
    private final TrackingService trackingService;
    private final JwtService jwtService;

    @Value(value = "${auth.time_hold_sms}")
    private int TIME_HOLD_SMS;
    @Value(value = "${auth.count_number_code}")
    private int COUNT_NUMBER_CODE;
    @Value(value = "${auth.sms_code_live}")
    private int SMS_CODE_LIVE;
    @Value(value = "${project.url}")
    private String PROJECT_URL;

    @Override
    public int makeCode(String ipAddress, AuthSmsDto authSmsDto) throws AuthSmsException {
        var user = userService.findUserByPhone(authSmsDto.phone());
        if (user != null && !Strings.isBlank(user.getEmail())) {
            return makeCodeSms(ipAddress, authSmsDto);
        } else {
            throw new ApiException(USER_NOT_FOUND, HttpStatus.NOT_FOUND);
        }
    }

    private int makeCodeSms(String ipAddress, AuthSmsDto authSmsDto) throws AuthSmsException {
        LocalDateTime timeHoldSms = LocalDateTime.now().minusSeconds(TIME_HOLD_SMS);
        var lastSendSms = authSmsRepository.countByIpaddressAndCreatedAtAfter(ipAddress, timeHoldSms);
        if (lastSendSms > 0) {
            throw new AuthSmsException(MESSAGE_SMALL_INTERVAL);
        }
        String code = makeCodeForPhone();
        AuthSmsEntity authSmsEntity = AuthSmsEntity.builder()
                .ipaddress(ipAddress)
                .code(code)
                .phone(authSmsDto.phone())
                .createdAt(LocalDateTime.now())
                .build();
        authSmsRepository.save(authSmsEntity);
        messageService.send(authSmsDto.phone(), String.format(SMS_CODE_MESSAGE, code, PROJECT_URL));
        return TIME_HOLD_SMS;
    }

    @Override
    public int checkUserPhone(String ipAddress, AuthSmsDto authSmsDto) throws AuthSmsException {
        var user = userService.findUserByPhone(authSmsDto.phone());
        if (user == null || Strings.isBlank(user.getEmail())) {
            return makeCodeSms(ipAddress, authSmsDto);
        } else {
            throw new ApiException(USER_ALREADY_REGISTERED, HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public TokenModel validateCode(ValidateSmsDto validateSms) throws AuthSmsException {
        if (validateSms.code().length() != COUNT_NUMBER_CODE) {
            throw new AuthSmsException("Не верная длина кода");
        }
        if (!checkCode(validateSms.code(), validateSms.phone())) {
            throw new AuthSmsException("Не верный код");
        }
        var user = userService.findOrCreateByPhone(validateSms.phone());
        return jwtService.generateTokens(userDetailMapper.toUserDetail(user));
    }

    @Override
    public String getSms(String phone) {
        return authSmsRepository.findFirstByPhoneOrderByCreatedAtDesc(phone)
                .map(AuthSmsEntity::getCode)
                .orElse("no find code");
    }

    @Override
    @Transactional
    public void clearExpired() {
        LocalDateTime expireDate = LocalDateTime.now().minusSeconds(SMS_CODE_LIVE);
        log.info("remove expired sms: {}", authSmsRepository.deleteByCreatedAtBefore(expireDate));
        log.info("remove expired refresh token: {}", jwtService.removeExpiredTokens());
    }

    @Override
    public TokenModel login(LoginDto login) throws AuthSmsException {
        var user = userService.authenticate(login);
        return jwtService.generateTokens(userDetailMapper.toUserDetail(user));
    }

    @Override
    public TokenModel registration(RegistrationDto registration) {
        var token = validateCode(new ValidateSmsDto(registration.phone(), registration.code()));
        userService.registrationUser(registration);
        trackingService.updateListTracking(registration.phone());
        return token;
    }

    @Override
    public boolean logout(UUID refresh) {
        jwtService.removeToken(refresh);
        return true;
    }

    private String makeCodeForPhone() {
        String format = "%0" + COUNT_NUMBER_CODE + "d";
        return String.format(format, (int) (Math.random() * Math.pow(10, COUNT_NUMBER_CODE)));
    }

    private boolean checkCode(String code, String phone) {
        LocalDateTime timeExpired = LocalDateTime.now().minusSeconds(SMS_CODE_LIVE);
        return authSmsRepository.findFirstByPhoneAndCodeAndCreatedAtAfter(phone, code, timeExpired).isPresent();
    }
}
