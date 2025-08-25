package express.atc.backend.integration.smsaero.client;

import express.atc.backend.integration.smsaero.config.SmsAeroFeignConfig;
import express.atc.backend.integration.smsaero.dto.SmsAeroResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "smsAeroClient",
        url = "${sms-aero.url}",
        configuration = SmsAeroFeignConfig.class
)
public interface SmsAeroClient {

    @GetMapping("/sms/send")
    SmsAeroResponse sendSms(
            @RequestParam("number") String phone,
            @RequestParam("sign") String sign,
            @RequestParam("text") String message
    );
}