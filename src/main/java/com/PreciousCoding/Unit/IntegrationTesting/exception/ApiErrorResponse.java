package com.PreciousCoding.Unit.IntegrationTesting.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

@Setter
@Getter
public class ApiErrorResponse {
    private HttpStatus httpStatus;
    private String message;
    private String path;
    private String api;
    private ZonedDateTime timestamp;

    public ApiErrorResponse() {
    }

    public ApiErrorResponse(HttpStatus httpStatus, String message, String path, String api, ZonedDateTime timestamp) {
        this.httpStatus = httpStatus;
        this.message = message;
        this.path = path;
        this.api = api;
        this.timestamp = timestamp;
    }

}