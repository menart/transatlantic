package express.atc.backend.rabbitmq.service;

import express.atc.backend.rabbitmq.dto.PersonInfoNeedDto;
import express.atc.backend.service.TrackingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RabbitMqService {

    private final TrackingService trackingService;

    public void processing(PersonInfoNeedDto dto) {
        trackingService.updateByOrderCode(
                dto.getLogisticsOrderCode() != null ? dto.getLogisticsOrderCode() : dto.getTrackingNumber()
        );
    }


    public void processingNeedDocument(PersonInfoNeedDto dto) {
        trackingService.setStatusNeedDocument(dto.getLogisticsOrderCode());
    }
}
