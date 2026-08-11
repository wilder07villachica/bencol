package com.runicsoft.bencolapp.egresos.models;

import com.runicsoft.bencolapp.egresos.utils.CategoriaEgreso;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "egresos")
@Data
public class Egreso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaEgreso categoria;

    @Column(nullable = false, length = 255)
    private String concepto;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;

    @Column(length = 100)
    private String referencia;

    @Column(name = "fecha_egreso", nullable = false)
    private LocalDateTime fechaEgreso;

    @Column(name = "registrado_por", length = 50)
    private String registradoPor;

    @PrePersist
    public void prePersist() {
        fechaEgreso = LocalDateTime.now();
    }
}