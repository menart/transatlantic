package express.atc.backend.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;
import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum DocumentType {
    PASSPORT(21, "Паспорт РФ"),
    PASSPORT_FOREIGN(10, "Паспорт иностранного гражданина"),
    RESIDENCE_PERMIT(12, "Вид на жительство в РФ"),
    TEMPORARY_RESIDENCE_PERMIT(15, "Разрешение на временное проживание в РФ"),
    REFUGEE_CERTIFICATE(19, "Свидетельство о предоставлении временного убежища на территории РФ"),
    BIRTH_CERTIFICATE(3, "Свидетельство о рождении"),
    BIRTH_CERTIFICATE_FOREIGN(23, "Свидетельство о рождении, выданное уполномоченным органом иностранного государства");

    private final Integer id;
    private final String name;

    public static DocumentType getDocumentTypeById(Integer id) {
        if (id == null) {
            return null;
        }
        return Stream.of(values())
                .filter(documentType -> Objects.equals(documentType.getId(), id))
                .findFirst()
                .orElse(null);
    }

    public static DocumentType getDocumentTypeByName(String name) {
        if (name == null) {
            return null;
        }
        return Stream.of(values())
                .filter(documentType -> Objects.equals(documentType.getName(), name))
                .findFirst()
                .orElse(null);
    }
}
