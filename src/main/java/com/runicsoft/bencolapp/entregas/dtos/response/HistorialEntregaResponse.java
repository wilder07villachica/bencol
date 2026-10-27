package com.runicsoft.bencolapp.entregas.dtos.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HistorialEntregaResponse {

    private Long id;
    private String accion;
    private String estadoAnterior;
    private String estadoNuevo;
    private String observaciones;
    private String usuario;
    private LocalDateTime fecha;
}
