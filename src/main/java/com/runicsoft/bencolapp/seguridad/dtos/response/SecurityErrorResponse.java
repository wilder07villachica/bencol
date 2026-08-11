package com.runicsoft.bencolapp.seguridad.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class SecurityErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
}