package express.atc.backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import express.atc.backend.db.entity.UserEntity;
import express.atc.backend.serializer.LocalDateValidDeserializer;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import static express.atc.backend.Constants.DATE_NOT_VALID;
import static express.atc.backend.Constants.DOC_TYPE_NOT_VALID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
public class DocumentDto {
    @JsonIgnore
    private Long id;
    @JsonIgnore
    private UserEntity user;
    @NotNull(message = DOC_TYPE_NOT_VALID)
    @Schema(description = "Тип документа:     <br/>" +
            "\"21 - Паспорт РФ\",<br/>" +
            "\"10 - Паспорт иностранного гражданина\",<br/>" +
            "\"12 - Вид на жительство в РФ\",<br/>" +
            "\"15 - Разрешение на временное проживание в РФ\",<br/>" +
            "\"19 - Свидетельство о предоставлении временного убежища на территории РФ\",<br/>" +
            "\"3  - Свидетельство о рождении\",<br/>" +
            "\"23 - Свидетельство о рождении, выданное уполномоченным органом иностранного государства\"",
            type = "int")
    private Integer typeId;
    @Schema(description = "Тип документа:     <br/>" +
            "\"Паспорт РФ\",<br/>" +
            "\"Паспорт иностранного гражданина\",<br/>" +
            "\"Вид на жительство в РФ\",<br/>" +
            "\"Разрешение на временное проживание в РФ\",<br/>" +
            "\"Свидетельство о предоставлении временного убежища на территории РФ\",<br/>" +
            "\"Свидетельство о рождении\",<br/>" +
            "\"Свидетельство о рождении, выданное уполномоченным органом иностранного государства\"",
            type = "string")
    private String type;
    @Schema(description = "Серия документа")
    private String series;
    @Schema(description = "Номер документа")
    private String number;
    @Schema(description = "Код подразделения")
    private String idDepartment;
    @Schema(description = "Орган, выдавший документ")
    private String nameDepartment;
    @Schema(description = "Дата выдачи документа")
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateValidDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @NotNull(message = "Дата выдачи документа: " + DATE_NOT_VALID)
    private LocalDate issueDate;
    @Schema(description = "Дата, до которой действует документ")
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonDeserialize(using = LocalDateValidDeserializer.class)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate expiredDate;
}
