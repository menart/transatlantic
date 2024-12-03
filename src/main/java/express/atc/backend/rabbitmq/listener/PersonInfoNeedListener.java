package express.atc.backend.rabbitmq.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import express.atc.backend.rabbitmq.dto.PersonInfoNeedDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class PersonInfoNeedListener {

    private final ObjectMapper objectMapper;

    @RabbitListener(queues = "${spring.rabbitmq.person-info-need.queue}")
    public void listener(String message){
        try {
            PersonInfoNeedDto dto = objectMapper.readValue(message, PersonInfoNeedDto.class);
            System.out.println(dto);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
//            throw new RuntimeException(e);
        }
    }
}
