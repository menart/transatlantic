package express.atc.backend.service;

import express.atc.backend.dto.FeedbackFieldDto;
import express.atc.backend.enums.FeedbackType;

import java.util.List;

public interface FeedbackService {

    FeedbackFieldDto saveFeedback(FeedbackFieldDto feedbackField, FeedbackType feedbackType, String userPhone);

    List<FeedbackFieldDto> getListFeedback(String userPhone);
}
