package com.runicsoft.bencolapp.productos.dtos.request;

import com.runicsoft.bencolapp.productos.utils.ProductoCategoria;
import com.runicsoft.bencolapp.utils.EstadoGeneral;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductoRequest {

    @NotBlank(message = "El código es obligatorio.")
    @Size(max = 20, message = "El código no debe exceder los 20 caracteres.")
    private String codigo;

    @NotBlank(message = "La descripción es obligatoria.")
    @Size(max = 255, message = "La descripción no debe exceder los 255 caracteres.")
    private String descripcion;

    @NotNull(message = "La categoría es obligatoria.")
    private ProductoCategoria categoria;

    @NotNull(message = "El precio base es obligatorio.")
    @DecimalMin(value = "0.01", message = "El precio base debe ser mayor que cero.")
    private BigDecimal precioBase;

    private EstadoGeneral estado;
}