package com.runicsoft.bencolapp.entregas.dtos.response;

import lombok.Data;

@Data
public class ProductoEntregaResponse {

    private Long productoId;
    private String codigo;
    private String descripcion;
    private Integer cantidad;
}
