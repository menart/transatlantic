package express.atc.backend.integration.cargoflow.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ConditionDto {
    private String group;
    private String property;
    private String operator;
    private String value;
    private List<ConditionDto> conditions;

    public ConditionDto(String property, String operator, String value) {
        this.property = property;
        this.operator = operator;
        this.value = value;
    }

    public ConditionDto(List<ConditionDto> conditions, String group) {
        this.conditions = conditions;
        this.group = group;
    }
}
