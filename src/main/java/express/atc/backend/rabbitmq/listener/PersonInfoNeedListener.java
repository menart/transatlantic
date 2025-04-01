package express.atc.backend.rabbitmq.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import express.atc.backend.rabbitmq.dto.PersonInfoNeedDto;
import express.atc.backend.rabbitmq.service.RabbitMqService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Argument;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PersonInfoNeedListener {

    private final ObjectMapper objectMapper;
    private final RabbitMqService rabbitMqService;

    @RabbitListener(
            queuesToDeclare = @Queue(
                    name = "${spring.rabbitmq.person-info-need.queue}",
                    durable = "true",
                    arguments = @Argument(name = "x-queue-type", value = "classic")
            )
    )
    public void listener(String message) {
        try {
            PersonInfoNeedDto dto = objectMapper.readValue(message, PersonInfoNeedDto.class);
            log.info("{}", dto);
            rabbitMqService.processing(dto);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
        }
    }
}
