package com.roman.sapun.java.socialmedia.controller;

import com.roman.sapun.java.socialmedia.dto.ResponseExceptionDTO;
import com.roman.sapun.java.socialmedia.exception.TokenExpiredException;
import com.roman.sapun.java.socialmedia.exception.ValuesAreNotEqualException;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.Timestamp;
import java.time.ZonedDateTime;

@RestControllerAdvice
public class HandlerController {
    @ExceptionHandler(value = {DataIntegrityViolationException.class, Exception.class})
    protected ResponseEntity<ResponseExceptionDTO> handleCommonException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ResponseExceptionDTO(ex.getClass().getName(),
                        Timestamp.from(ZonedDateTime.now().toInstant()),
                        ex.getMessage()));
    }

    @ExceptionHandler(value = {ValuesAreNotEqualException.class, TokenExpiredException.class})
    protected ResponseEntity<ResponseExceptionDTO> handleValidationException(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(new ResponseExceptionDTO(ex.getClass().getName(),
                        Timestamp.from(ZonedDateTime.now().toInstant()),
                        ex.getMessage()));
    }

    @ExceptionHandler(value = {InternalAuthenticationServiceException.class, ExpiredJwtException.class,
            BadCredentialsException.class})
    public ResponseEntity<ResponseExceptionDTO> handleUnauthorizedException(RuntimeException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new ResponseExceptionDTO(ex.getClass().getName(),
                        Timestamp.from(ZonedDateTime.now().toInstant()),
                        ex.getMessage()));
    }
}