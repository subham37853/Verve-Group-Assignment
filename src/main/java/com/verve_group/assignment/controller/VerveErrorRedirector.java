package com.verve_group.assignment.controller;

import com.verve_group.assignment.model.VerveBaseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class VerveErrorRedirector {

    @ExceptionHandler
    public ResponseEntity<VerveBaseException> handleException(Exception ex) {
        // Log the exception
        System.out.println("An error occurred: " + ex.getMessage());
        return new ResponseEntity<>(new VerveBaseException(ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
