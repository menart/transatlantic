package express.atc.backend.integration.cargoflow.service;

import express.atc.backend.integration.cargoflow.dto.CargoflowToken;

public interface AuthService {

    CargoflowToken getToken();
}
