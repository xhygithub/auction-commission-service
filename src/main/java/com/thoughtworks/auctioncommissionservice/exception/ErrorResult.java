package com.thoughtworks.auctioncommissionservice.exception;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorResult {

    @JsonProperty("error_code")
    private String errorCode;

    private String message;

    public ErrorResult(ErrorCode errorCode) {
        this.errorCode = errorCode.getValue();
    }

    public ErrorResult(ErrorCode errorCode, String message) {
        this(errorCode);
        this.message = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }
}