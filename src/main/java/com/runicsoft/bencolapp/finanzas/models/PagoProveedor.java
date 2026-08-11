package com.runicsoft.bencolapp.finanzas.models;

import com.runicsoft.bencolapp.finanzas.utils.MetodoPago;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagos_proveedores")
@Data
public class PagoProveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cuenta_pagar_id", nullable = false)
    private CuentaPagar cuentaPagar;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", nullable = false)
    private MetodoPago metodoPago;

    @Column(length = 100)
    private String referencia;

    @Column(name = "fecha_pago", nullable = false)
    private LocalDateTime fechaPago;

    @Column(name = "registrado_por", length = 50)
    private String registradoPor;

    @PrePersist
    public void prePersist() {
        fechaPago = LocalDateTime.now();
    }
}