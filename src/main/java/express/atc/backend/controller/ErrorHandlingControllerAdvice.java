package express.atc.backend.controller;

import express.atc.backend.dto.ErrorResponseDto;
import express.atc.backend.exception.ApiException;
import express.atc.backend.exception.TrackNotFoundException;
import express.atc.backend.exception.UnauthorizedException;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@ControllerAdvice
@Hidden
public class ErrorHandlingControllerAdvice {

    @ExceptionHandler(ConversionFailedException.class)
    public ResponseEntity<String> handleConflict(RuntimeException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponseDto handleValidationErrors(MethodArgumentNotValidException ex) {
        Stream<String> fieldErrors = ex.getFieldErrors().stream()
                .map(e -> String.format("%s", e.getDefaultMessage()));

        Stream<String> globalErrors = ex.getGlobalErrors().stream()
                .map(e -> String.format("%s", e.getDefaultMessage()));

        List<String> error = Stream.concat(fieldErrors, globalErrors).toList();

        return new ErrorResponseDto(HttpStatus.BAD_REQUEST.getReasonPhrase(), error);
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AuthenticationException.class)
    public ErrorResponseDto handleForbidden() {
        return new ErrorResponseDto(HttpStatus.FORBIDDEN.getReasonPhrase(),
                Collections.singletonList("Доступ запрещен"));
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(TrackNotFoundException.class)
    public ErrorResponseDto handleApiException(ApiException ex) {
        return new ErrorResponseDto(HttpStatus.NOT_FOUND.getReasonPhrase(),
                Collections.singletonList(ex.getMessage()));
    }

    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    @ExceptionHandler(MessagingException.class)
    public ErrorResponseDto handleMessagingException(MessagingException ex) {
        return new ErrorResponseDto(HttpStatus.BAD_GATEWAY.getReasonPhrase(),
                Collections.singletonList("Проблема при отправки почты: " + ex.getMessage()));
    }

    @ExceptionHandler(ApiException.class)
    @ResponseBody
    public ResponseEntity<?> onApiException(ApiException e) {
        log.warn("Exception ", e);
        return new ResponseEntity<>(
                new ErrorResponseDto(
                        e.getStatus().name(),
                        Collections.singletonList(e.getMessage())
                ),
                e.getStatus()
        );
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseBody
    public ResponseEntity<?> onUnauthorizedException(UnauthorizedException e) {
        log.warn("Exception {}", e.getMessage());
        return new ResponseEntity<>(
                new ErrorResponseDto(
                        HttpStatus.NOT_FOUND.name(),
                        Collections.singletonList(e.getMessage())
                ),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ResponseEntity<ErrorResponseDto> onException(Exception exception) {
        log.warn("Exception ", exception);
        return new ResponseEntity<>(
                new ErrorResponseDto(
                        "error",
                        Collections.singletonList(exception.getMessage())
                ),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

}
