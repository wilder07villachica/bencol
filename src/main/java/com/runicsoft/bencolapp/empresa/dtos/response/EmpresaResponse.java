package com.runicsoft.bencolapp.empresa.dtos.response;

import lombok.Data;

@Data
public class EmpresaResponse {
    private Long id;
    private String ruc;
    private String razonSocial;
    private String nombreComercial;
    private String direccion;
    private String telefono;
    private String email;
    private String logoNombre;
    private String logoTipo;
    private String estado;
}