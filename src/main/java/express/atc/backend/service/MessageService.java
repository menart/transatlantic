package express.atc.backend.service;

import express.atc.backend.dto.UserDto;

public interface MessageService {

    void send(String phone, String message);
}
