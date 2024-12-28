package express.atc.backend.integration.cargoflow.service;

import express.atc.backend.dto.TrackingDto;
import express.atc.backend.dto.TrackingRouteDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public interface CargoflowService {

    List<TrackingDto> getInfoByTrackNumber(String trackNumber);
    List<TrackingDto> getInfoByLogisticsOrderCode(String logisticsOrderCode);
    TreeSet<TrackingDto> getSetInfoByPhone(String userPhone);
    Set<TrackingRouteDto> updateRoute(Long orderId, Long historyId);
    void uploadFile(MultipartFile file, String logisticsOrderCode);
}
