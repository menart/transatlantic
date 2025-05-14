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

    TrackingDto find(String number) throws TrackNotFoundException;

    CalculateDto calc(String trackNumber);

    TrackingPageDto list(Integer page, int count, TrackingStatus filter);

    boolean uploadFile(MultipartFile file, String trackNumber);
    boolean uploadFiles(MultipartFile[] file, String trackNumber);

    Set<TrackingDto> getAllTrackByPhone(String phoneNumber);

    TrackingNeedingDto need();

    void updateByOrderCode(String orderCode, String status);

    void updateListTracking(String userPhone);

    boolean paymentConfirmation(Long orderId);

    String paymentControl(String outSum, Long orderId, String trackingNumber, String checkSum);
}
