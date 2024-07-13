package express.atc.backend.controller;

import express.atc.backend.dto.ErrorResponseDto;
import express.atc.backend.exception.AuthSmsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(AuthSmsException.class)
    public ErrorResponseDto handleAuthSmsException(AuthSmsException ex) {
        return new ErrorResponseDto(HttpStatus.BAD_REQUEST.getReasonPhrase(),
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
