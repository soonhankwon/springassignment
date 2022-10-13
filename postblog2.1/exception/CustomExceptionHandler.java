package com.assignment.postblog.exception;

import com.assignment.postblog.dto.MemberResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(value = {CustomException.class})
    public ResponseEntity<Object> handleAdminErrorException(CustomException exception) {
        log.error("throw customException : {}", exception.getErrorCode());
        MemberResponseDto restApiException = new MemberResponseDto(exception.getErrorCode().getHttpStatus().value(), exception.getErrorCode().getDetail(), "");
        return new ResponseEntity<>(restApiException, exception.getErrorCode().getHttpStatus());
    }
}
