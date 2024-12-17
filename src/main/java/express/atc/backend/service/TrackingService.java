package express.atc.backend.service;

import express.atc.backend.dto.CalculateDto;
import express.atc.backend.dto.PageDto;
import express.atc.backend.dto.TrackingDto;
import express.atc.backend.enums.TrackingStatus;
import express.atc.backend.exception.TrackNotFoundException;
import org.springframework.web.multipart.MultipartFile;

public interface TrackingService {

    TrackingDto find(String trackNumber, String userPhone) throws TrackNotFoundException;

    CalculateDto calc(String trackNumber, String userPhone);

    PageDto<TrackingDto> list(Integer page, int count, String userPhone, TrackingStatus filter);

    boolean uploadFile(MultipartFile file, String trackNumber);
}
