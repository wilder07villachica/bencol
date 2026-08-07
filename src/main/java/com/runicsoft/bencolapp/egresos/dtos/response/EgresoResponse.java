package com.runicsoft.bencolapp.egresos.dtos.response;

import com.runicsoft.bencolapp.egresos.utils.CategoriaEgreso;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class EgresoResponse {
    private Long id;
    private CategoriaEgreso categoria;
    private String concepto;
    private BigDecimal monto;
    private String referencia;
    private LocalDateTime fechaEgreso;
}