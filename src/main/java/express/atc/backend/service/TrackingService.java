package express.atc.backend.service;

import express.atc.backend.dto.CalculateDto;
import express.atc.backend.dto.TrackingDto;
import express.atc.backend.dto.TrackingNeedingDto;
import express.atc.backend.dto.TrackingPageDto;
import express.atc.backend.enums.TrackingStatus;
import express.atc.backend.exception.TrackNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

public interface TrackingService {

    TrackingDto find(String trackNumber, String userPhone) throws TrackNotFoundException;

    CalculateDto calc(String trackNumber, String userPhone);

    TrackingPageDto list(Integer page, int count, String userPhone, TrackingStatus filter);

    boolean uploadFile(MultipartFile file, String trackNumber);

    Set<TrackingDto> getAllTrackByPhone(String phoneNumber);

    TrackingNeedingDto need(String userPhone);

    void updateByLogisticCode(String logisticsOrderCode);

    void updateListTracking(String userPhone);

    boolean paymentConfirmation(String trackNumber, String userPhone);
}
