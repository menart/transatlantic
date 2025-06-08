package express.atc.backend.integration.cargoflow.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CargoflowView {
    LOCAL("_local"),
    ORDER_LIST("order-list-view");

    private final String view;
}
