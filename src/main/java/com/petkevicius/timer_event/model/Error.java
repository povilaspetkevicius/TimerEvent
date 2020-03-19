package com.petkevicius.timer_event.model;

import org.springframework.http.HttpStatus;

public class Error {

    private String message;

    private HttpStatus code;

    public Error(String message, HttpStatus code) {
        this.message = message;
        this.code = code;
    }
}
