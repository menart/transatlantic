package express.atc.backend.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

import static express.atc.backend.Constants.AUTH_HEADER_NAME;
import static express.atc.backend.Constants.BEARER_PREFIX;

@Slf4j
abstract class PrivateController {

    protected String getToken() {
        var header = ((ServletRequestAttributes) Objects.requireNonNull(
                RequestContextHolder.getRequestAttributes()))
                .getRequest().getHeader(AUTH_HEADER_NAME);
        return header.substring(BEARER_PREFIX.length());
    }
}
