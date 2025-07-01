package express.atc.backend.rabbitmq.service;

import express.atc.backend.rabbitmq.dto.PersonInfoNeedDto;
import express.atc.backend.service.TrackingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class RabbitMqService {

    private final TrackingService trackingService;

    public void processingStatus(PersonInfoNeedDto dto) {
        var order = dto.getLogisticsOrderCode() != null ? dto.getLogisticsOrderCode() : dto.getTrackingNumber();
        try {
            trackingService.updateByOrderCode(order);
        } catch (Exception e) {
            log.error("processingStatus: {} error {}", order, e.getMessage());
        }
    }


    public void processingNeedDocument(PersonInfoNeedDto dto) {
        try {
            trackingService.setStatusFirstNeedDocument(dto.getLogisticsOrderCode());
        } catch (Exception e) {
            log.error("processingNeedDocument: {} error: {}", dto.getLogisticsOrderCode(), e.getMessage());
        }

    }
}
