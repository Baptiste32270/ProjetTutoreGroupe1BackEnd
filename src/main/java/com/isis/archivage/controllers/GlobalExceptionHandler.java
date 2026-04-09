package com.isis.archivage.controllers;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, String>> handleMaxSizeException(MaxUploadSizeExceededException exc) {

        Map<String, String> reponse = new HashMap<>();
        reponse.put("erreur", "Fichier trop volumineux");
        reponse.put("message", "Le document dépasse la limite autorisée (5 Mo maximum).");

        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(reponse);
    }
}