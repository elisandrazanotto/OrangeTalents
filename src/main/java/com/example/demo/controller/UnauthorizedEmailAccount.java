package com.example.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class UnauthorizedEmailAccount extends Exception{

    public UnauthorizedEmailAccount(String message) {
        super(message);
    }

}
