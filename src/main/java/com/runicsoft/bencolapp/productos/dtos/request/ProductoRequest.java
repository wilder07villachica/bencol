package com.runicsoft.bencolapp.productos.dtos.request;

import com.runicsoft.bencolapp.productos.utils.ProductoCategoria;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductoRequest {
    private String codigo;
    private String descripcion;
    private ProductoCategoria  categoria;
    private BigDecimal precioBase;
}
