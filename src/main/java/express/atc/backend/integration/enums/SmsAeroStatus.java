package express.atc.backend.integration.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SmsAeroStatus {
    IN_QUEUE(0),
    DELIVERED(1),
    NOT_DELIVERED(2),
    SEND(3),
    WAITING(4),
    REJECT(6),
    MODERATION(8);

    private final int status;
}
