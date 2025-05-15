package express.atc.backend.dto;

import express.atc.backend.enums.Language;
import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@RequestScope
@Component
@Data
public class RequestInfo {

    UserDto user;
    Language language;
}
