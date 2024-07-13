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
import express.atc.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthSmsRepository authSmsRepository;
    private final UserService userService;
    private final UserDetailMapper userDetailMapper;
    private final JwtService jwt;

    @Value(value = "${constant.time_hold_sms}")
    private int TIME_HOLD_SMS;
    @Value(value = "${constant.count_number_code}")
    private int COUNT_NUMBER_CODE;
    @Value(value = "${constant.sms_code_live}")
    private int SMS_CODE_LIVE;

    @Override
    public int makeCode(String ipAddress, AuthSmsDto authSmsDto) throws AuthSmsException {
        LocalDateTime timeHoldSms = LocalDateTime.now().minusSeconds(TIME_HOLD_SMS);
        var lastSendSms = authSmsRepository.countByIpaddressAndCreatedAtAfter(ipAddress, timeHoldSms);
        if (lastSendSms > 0) {
            throw new AuthSmsException("Слишком маленький интервал запроса");
        }
        String code = makeCodeForPhone();
        AuthSmsEntity authSmsEntity = AuthSmsEntity.builder()
                .ipaddress(ipAddress)
                .code(code)
                .phone(authSmsDto.getPhone())
                .createdAt(LocalDateTime.now())
                .build();
        authSmsRepository.save(authSmsEntity);
        return TIME_HOLD_SMS;
    }

    @Override
    public JwtAuthenticationResponse validateCode(ValidateSmsDto validateSms) throws AuthSmsException {
        if (validateSms.getCode().length() != COUNT_NUMBER_CODE) {
            throw new AuthSmsException("Не верная длина кода");
        }
        if (!checkCode(validateSms.getCode(), validateSms.getPhone())) {
            throw new AuthSmsException("Не верный код");
        }
        var user = userService.findOrCreateByPhone(validateSms.getPhone());
        return JwtAuthenticationResponse.builder()
                .token(jwt.generateToken(userDetailMapper.toUserDetail(user)))
                .build();
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
