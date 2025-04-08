package express.atc.backend.service.Impl;

import express.atc.backend.db.entity.FeedbackEntity;
import express.atc.backend.db.repository.FeedbackRepository;
import express.atc.backend.dto.FeedbackFieldDto;
import express.atc.backend.enums.FeedbackType;
import express.atc.backend.exception.ApiException;
import express.atc.backend.service.EmailService;
import express.atc.backend.service.FeedbackService;
import express.atc.backend.service.UserService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static express.atc.backend.Constants.EMAIL_NOT_GIVEN;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final EmailService emailService;
    private final UserService userService;

    @Value("${feedback.template}")
    private String template;
    @Value("${feedback.title}")
    private String title;

    @Override
    public FeedbackFieldDto saveFeedback(FeedbackFieldDto feedbackField, FeedbackType feedbackType, String userPhone) {
        var user = userService.findUserByPhone(userPhone);
        if(user.getEmail() == null) {
            throw new ApiException(EMAIL_NOT_GIVEN, HttpStatus.BAD_REQUEST);
        }
        FeedbackEntity entity = FeedbackEntity.builder()
                .userId(user.getId())
                .type(feedbackType)
                .feedback(feedbackField)
                .build();
        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("name", feedbackField.name());
        templateModel.put("text", feedbackField.body());
        try {
            emailService.sendMessageUsingTemplate(user.getEmail(), title, templateModel, template);
        } catch (MessagingException exception) {
            log.error(exception.getMessage());
        }
        return feedbackRepository.save(entity).getFeedback();
    }

    @Override
    public List<FeedbackFieldDto> getListFeedback(String userPhone) {
        var user = userService.findUserByPhone(userPhone);
        return feedbackRepository.findByUserId(user.getId()).stream()
                .map(FeedbackEntity::getFeedback)
                .toList();
    }
}
