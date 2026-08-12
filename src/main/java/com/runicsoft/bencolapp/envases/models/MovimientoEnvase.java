package com.runicsoft.bencolapp.envases.models;

import com.runicsoft.bencolapp.envases.utils.TipoMovimientoEnvase;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "movimientos_envases")
@Data
public class MovimientoEnvase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cuenta_envase_id", nullable = false)
    private CuentaEnvasesCliente cuentaEnvase;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_movimiento", nullable = false)
    private TipoMovimientoEnvase tipoMovimiento;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(length = 255)
    private String referencia;

    @Column(name = "fecha_movimiento", nullable = false)
    private LocalDateTime fechaMovimiento;

    @Column(name = "registrado_por", length = 50)
    private String registradoPor;

    @PrePersist
    public void prePersist() {
        fechaMovimiento = LocalDateTime.now();
    }
}