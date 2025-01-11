package express.atc.backend.service.Impl;

import express.atc.backend.db.entity.AuthSmsEntity;
import express.atc.backend.db.repository.AuthSmsRepository;
import express.atc.backend.dto.AuthSmsDto;
import express.atc.backend.dto.JwtAuthenticationResponse;
import express.atc.backend.dto.ValidateSmsDto;
import express.atc.backend.exception.AuthSmsException;
import express.atc.backend.mapper.UserDetailMapper;
import express.atc.backend.service.AuthService;
import express.atc.backend.service.JwtService;
import express.atc.backend.service.MessageService;
import express.atc.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.LocalDateTime;

import static express.atc.backend.Constants.MESSAGE_SMALL_INTERVAL;
import static express.atc.backend.Constants.SMS_CODE_MESSAGE;

@Service
@CrossOrigin
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthSmsRepository authSmsRepository;
    private final UserService userService;
    private final UserDetailMapper userDetailMapper;
    private final MessageService messageService;
    private final JwtService jwt;

    @Value(value = "${auth.time_hold_sms}")
    private int TIME_HOLD_SMS;
    @Value(value = "${auth.count_number_code}")
    private int COUNT_NUMBER_CODE;
    @Value(value = "${auth.sms_code_live}")
    private int SMS_CODE_LIVE;

    @Override
    public int makeCode(String ipAddress, AuthSmsDto authSmsDto) throws AuthSmsException {
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
        messageService.send(authSmsDto.phone(), String.format(SMS_CODE_MESSAGE, code));
        return TIME_HOLD_SMS;
    }

    @Override
    public JwtAuthenticationResponse validateCode(ValidateSmsDto validateSms) throws AuthSmsException {
        if (validateSms.code().length() != COUNT_NUMBER_CODE) {
            throw new AuthSmsException("Не верная длина кода");
        }
        if (!checkCode(validateSms.code(), validateSms.phone())) {
            throw new AuthSmsException("Не верный код");
        }
        var user = userService.findOrCreateByPhone(validateSms.phone());
        return JwtAuthenticationResponse.builder()
                .token(jwt.generateToken(userDetailMapper.toUserDetail(user)))
                .isFull(user.isFull())
                .build();
    }

    @Override
    public String getSms(String phone) {
        return authSmsRepository.findFirstByPhoneOrderByCreatedAtDesc(phone)
                .map(AuthSmsEntity::getCode)
                .orElse("no find code");
    }

    @Override
    @Transactional
    public void clearAuthCode() {
        LocalDateTime expireDate = LocalDateTime.now().minusSeconds(SMS_CODE_LIVE);
        log.info("remove expired sms: {}", authSmsRepository.deleteByCreatedAtBefore(expireDate));
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
