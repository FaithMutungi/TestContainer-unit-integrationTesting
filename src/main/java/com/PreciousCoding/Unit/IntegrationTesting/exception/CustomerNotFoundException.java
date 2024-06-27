package com.PreciousCoding.Unit.IntegrationTesting.exception;

public class CustomerNotFoundException extends RuntimeException{
    public CustomerNotFoundException(String message) {
        super(message);
    }
}