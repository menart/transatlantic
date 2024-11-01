package express.atc.backend.controller;

import express.atc.backend.dto.ErrorResponseDto;
import express.atc.backend.exception.ApiException;
import express.atc.backend.exception.AuthSmsException;
import express.atc.backend.exception.TrackNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
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
public class ErrorHandlingControllerAdvice {

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
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(AuthSmsException.class)
    public ErrorResponseDto handleAuthSmsException(AuthSmsException ex) {
        return new ErrorResponseDto(HttpStatus.UNAUTHORIZED.getReasonPhrase(),
                Collections.singletonList(ex.getMessage()));
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

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    ErrorResponseDto onException(Exception e) {
        log.warn("Exception ", e);
        return new ErrorResponseDto("error", Collections.singletonList(e.getMessage()));
    }


}
