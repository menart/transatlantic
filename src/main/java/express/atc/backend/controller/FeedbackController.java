package express.atc.backend.controller;

import express.atc.backend.dto.ErrorResponseDto;
import express.atc.backend.dto.FeedbackFieldDto;
import express.atc.backend.dto.RequestInfo;
import express.atc.backend.enums.FeedbackType;
import express.atc.backend.service.FeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping(path = "/api/feedback", produces = "application/json")
@Tag(name = "Feedback controller", description = "Контроллер для работы c обратной связью")
public class FeedbackController extends PrivateController {

    private final FeedbackService feedbackService;
    private final RequestInfo requestInfo;

    @Operation(summary = "Запись сообщения для обратной связи")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Сообщение обратной связи",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = FeedbackFieldDto.class))}),
            @ApiResponse(responseCode = "400",
                    description = "Невалидные параметры в запросе",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class))}),
    })
    @PostMapping
    public FeedbackFieldDto saveFeedback(@RequestBody FeedbackFieldDto feedbackField) {
        String userPhone = requestInfo.getUser() != null ? requestInfo.getUser().getPhone() : null;
        return feedbackService.saveFeedback(feedbackField, FeedbackType.QUESTION, userPhone);
    }

    @Operation(summary = "Получить список сообщений для обратной связи")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                    description = "Список сообщений обратной связи",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = FeedbackFieldDto.class)))
                    }),
            @ApiResponse(responseCode = "400",
                    description = "Невалидные параметры в запросе",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponseDto.class))}),
    })
    @GetMapping
    public List<FeedbackFieldDto> getListFeedback() {
        String userPhone = requestInfo.getUser() != null ? requestInfo.getUser().getPhone() : null;
        return feedbackService.getListFeedback(userPhone);
    }
}
