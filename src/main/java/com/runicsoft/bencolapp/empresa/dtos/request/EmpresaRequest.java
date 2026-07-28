package com.runicsoft.bencolapp.empresa.dtos.request;

import lombok.Data;

@Data
public class EmpresaRequest {
    private String ruc;
    private String razonSocial;
    private String nombreComercial;
}
