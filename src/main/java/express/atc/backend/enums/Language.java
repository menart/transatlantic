package express.atc.backend.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Objects;
import java.util.stream.Stream;

@RequiredArgsConstructor
@Getter
public enum Language {
    EN("en"),
    RU("ru");

    private final String name;

    public static Language getLanguage(String name) {
        if (name == null) {
            return null;
        }
        return Stream.of(values())
                .filter(language -> Objects.equals(language.getName(), name))
                .findFirst()
                .orElse(null);
    }
}
