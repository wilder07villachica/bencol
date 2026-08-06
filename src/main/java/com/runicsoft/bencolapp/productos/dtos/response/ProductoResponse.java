package com.runicsoft.bencolapp.productos.dtos.response;

import com.runicsoft.bencolapp.productos.utils.ProductoCategoria;
import com.runicsoft.bencolapp.productos.utils.UnidadMedida;
import com.runicsoft.bencolapp.utils.EstadoGeneral;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductoResponse {
    private Long id;
    private String codigo;
    private String descripcion;
    private ProductoCategoria categoria;
    private BigDecimal contenido;
    private UnidadMedida unidadMedida;
    private Integer unidadesPorPaquete;
    private BigDecimal precioBase;
    private EstadoGeneral estado;
}