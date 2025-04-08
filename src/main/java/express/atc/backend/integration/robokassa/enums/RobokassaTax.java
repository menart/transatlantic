package express.atc.backend.integration.robokassa.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RobokassaTax {
    NONE("none"),
    VAT0("vat0"),
    VAT10("vat10"),
    VAT110("vat110"),
    VAT20("vat20"),
    VAT120("vat120");

    private final String value;
}
