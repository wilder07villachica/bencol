package com.runicsoft.bencolapp.reportes.dtos.response;

import lombok.Data;

@Data
public class ResumenInventarioResponse {
    private Long cantidadProductosInventario;
    private Long productosStockBajo;
}