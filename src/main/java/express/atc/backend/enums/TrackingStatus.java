package express.atc.backend.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TrackingStatus {
    NEED_PAYMENT(true),
    NEED_DOCUMENT(false),
    PAYMENT_CONFIRMATION(false),
    ACTIVE(false),
    ARCHIVE(false),
    IGNORE(false);

    private final boolean isNeedAction;
}
