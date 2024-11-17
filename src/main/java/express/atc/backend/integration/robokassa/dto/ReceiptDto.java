package express.atc.backend.integration.robokassa.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import express.atc.backend.exception.ApiException;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Builder
@Data
@Slf4j
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public class ReceiptDto {

    private String sno;
    private List<ReceiptItemDto> items;

    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            log.info(mapper.writeValueAsString(this));
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new ApiException(e.getMessage());
        }
    }
}
