package express.atc.backend.service;

import express.atc.backend.dto.*;
import express.atc.backend.enums.TrackingStatus;
import express.atc.backend.exception.TrackNotFoundException;
import org.springframework.web.multipart.MultipartFile;

public interface TrackingService {

    TrackingDto find(String number) throws TrackNotFoundException;

    CalculateDto calc(String trackNumber);

    TrackingPageDto list(Integer page, int count, TrackingStatus filter);

    boolean uploadOneFile(MultipartFile file, String trackNumber);
    boolean uploadFiles(MultipartFile[] file, String trackNumber);

    Boolean getAllTrackByPhone();

    TrackingNeedingDto need();

    void updateByOrderCode(String orderCode);

    void setStatusFirstNeedDocument(String getLogisticsOrderCode);

    void updateListTracking(String userPhone);

    boolean paymentConfirmation(Long orderId);

    String paymentControl(String outSum, Long orderId, String orderNumber, String checkSum);

    Boolean setToArchive();

    TrackingAdminDto findByAdmin(String number);

    void sendUserInfo(UserDto responseUser);
}
