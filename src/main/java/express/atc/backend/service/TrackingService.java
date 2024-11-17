package express.atc.backend.service;

import express.atc.backend.dto.CalculateDto;
import express.atc.backend.dto.PageDto;
import express.atc.backend.dto.TrackingDto;
import express.atc.backend.exception.TrackNotFoundException;

public interface TrackingService {

    TrackingDto find(String trackNumber, String userPhone) throws TrackNotFoundException;

    CalculateDto calc(String trackNumber, String userPhone);

    PageDto<TrackingDto> list(Integer page, int count, String userPhone);
}
