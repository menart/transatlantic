package express.atc.backend.rabbitmq.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import express.atc.backend.rabbitmq.dto.PersonInfoNeedDto;
import express.atc.backend.rabbitmq.service.RabbitMqService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PersonInfoNeedListener {

    private final ObjectMapper objectMapper;
    private final RabbitMqService rabbitMqService;

    @RabbitListener(queues = "${spring.rabbitmq.person-info-need.queue-pd}")
    public void listenerPD(String message) {
        if (StringUtils.isNotEmpty(message)) {
            log.info("RabbitMQ pd read message: {}", message);
            processingNeedDocument(message);
        }
    }

    @RabbitListener(queues = "${spring.rabbitmq.person-info-need.queue-status}")
    public void listenerStatus(String message) {
        if (StringUtils.isNotEmpty(message)) {
            log.info("RabbitMQ status read message: {}", message);
            processing(message);
        }
    }

    private void processing(String message) {
        try {
            PersonInfoNeedDto dto = objectMapper.readValue(message, PersonInfoNeedDto.class);
            log.info("Rabbit MQ {}", dto);
            if (dto.getLogisticsOrderCode() != null || dto.getTrackingNumber() != null) {
                rabbitMqService.processing(dto);
            }
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
    }

    private void processingNeedDocument(String message) {
        try {
            PersonInfoNeedDto dto = objectMapper.readValue(message, PersonInfoNeedDto.class);
            log.info("Rabbit MQ Need Documents:  {}", dto);
            if (dto.getLogisticsOrderCode() != null || dto.getTrackingNumber() != null) {
                rabbitMqService.processingNeedDocument(dto);
            }
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
    }
}
