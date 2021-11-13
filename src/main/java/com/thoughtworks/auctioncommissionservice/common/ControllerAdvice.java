package com.thoughtworks.auctioncommissionservice.common;


import com.thoughtworks.auctioncommissionservice.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ControllerAdvice {

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorResult> handleOtherException(Throwable e) {
        return jsonContentResponseEntity(new ErrorResult(ErrorCode.SERVICE_UNAVAILABLE, e.getMessage()), HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(InsufficientFeePaymentException.class)
    public ResponseEntity<ErrorResult> handleInsufficientFeeException(InsufficientFeePaymentException e) {
        return jsonContentResponseEntity(new ErrorResult(ErrorCode.INSUFFICIENT_FEE, e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IncorrectInfoPaymentException.class)
    public ResponseEntity<ErrorResult> handleIncorrectInfoPaymentException(IncorrectInfoPaymentException e) {
        return jsonContentResponseEntity(new ErrorResult(ErrorCode.INCORRECT_ACCOUNT_INFO, e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(LotNotFoundException.class)
    public ResponseEntity<ErrorResult> handleRecordNotFoundException(LotNotFoundException e) {
        return jsonContentResponseEntity(new ErrorResult(ErrorCode.NOT_FOUND, e.getMessage()), HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<ErrorResult> jsonContentResponseEntity(final ErrorResult errorResult,
                                                                  final HttpStatus status) {
        final HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);
        return new ResponseEntity<>(errorResult, headers, status);
    }
}
