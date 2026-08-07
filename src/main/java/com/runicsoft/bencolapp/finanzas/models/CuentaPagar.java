package com.runicsoft.bencolapp.finanzas.models;

import com.runicsoft.bencolapp.compras.models.Compra;
import com.runicsoft.bencolapp.finanzas.utils.EstadoCuentaPagar;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "cuentas_pagar",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "compra_id")
        }
)
@Data
public class CuentaPagar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "compra_id", nullable = false, unique = true)
    private Compra compra;

    @Column(name = "monto_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoTotal;

    @Column(name = "monto_pagado", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoPagado;

    @Column(name = "saldo_pendiente", nullable = false, precision = 12, scale = 2)
    private BigDecimal saldoPendiente;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoCuentaPagar estado;

    @OneToMany(mappedBy = "cuentaPagar", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PagoProveedor> pagos = new ArrayList<>();

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @PrePersist
    public void prePersist() {
        fechaCreacion = LocalDateTime.now();

        if (montoPagado == null) {
            montoPagado = BigDecimal.ZERO;
        }
        if (estado == null) {
            estado = EstadoCuentaPagar.PENDIENTE;
        }
    }

    @PreUpdate
    public void preUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}