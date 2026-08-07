package com.runicsoft.bencolapp.caja.models;

import com.runicsoft.bencolapp.caja.utils.EstadoCaja;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "cajas")
@Data
public class Caja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "saldo_inicial", nullable = false, precision = 12, scale = 2)
    private BigDecimal saldoInicial;

    @Column(name = "total_ingresos", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalIngresos;

    @Column(name = "total_egresos", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalEgresos;

    @Column(name = "saldo_actual", nullable = false, precision = 12, scale = 2)
    private BigDecimal saldoActual;

    @Column(name = "saldo_esperado", precision = 12, scale = 2)
    private BigDecimal saldoEsperado;

    @Column(name = "saldo_real", precision = 12, scale = 2)
    private BigDecimal saldoReal;

    @Column(precision = 12, scale = 2)
    private BigDecimal diferencia;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoCaja estado;

    @OneToMany(mappedBy = "caja", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MovimientoCaja> movimientos = new ArrayList<>();

    @Column(name = "fecha_apertura", nullable = false)
    private LocalDateTime fechaApertura;

    @Column(name = "fecha_cierre")
    private LocalDateTime fechaCierre;

    @PrePersist
    public void prePersist() {
        fechaApertura = LocalDateTime.now();

        if (totalIngresos == null) {
            totalIngresos = BigDecimal.ZERO;
        }

        if (totalEgresos == null) {
            totalEgresos = BigDecimal.ZERO;
        }

        if (saldoActual == null) {
            saldoActual = saldoInicial;
        }

        if (estado == null) {
            estado = EstadoCaja.ABIERTA;
        }
    }
}