package com.runicsoft.bencolapp.cotizaciones.models;

import com.runicsoft.bencolapp.clientes.models.Cliente;
import com.runicsoft.bencolapp.cotizaciones.utils.EstadoCotizacion;
import com.runicsoft.bencolapp.empresa.models.Empresa;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "cotizaciones",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "codigo")
        }
)
@Data
public class Cotizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String codigo;

    @ManyToOne
    @JoinColumn(name = "empresa_id", nullable = false)
    private Empresa empresa;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "porcentaje_impuesto", nullable = false, precision = 5, scale = 2)
    private BigDecimal porcentajeImpuesto;

    @Column(name = "monto_impuesto", nullable = false, precision = 12, scale = 2)
    private BigDecimal montoImpuesto;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoCotizacion estado;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @Column(name = "condiciones_pago", length = 500)
    private String condicionesPago;

    @Column(name = "plazo_entrega", length = 255)
    private String plazoEntrega;

    @Column(length = 1000)
    private String observacion;

    @OneToMany(mappedBy = "cotizacion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleCotizacion> detalles = new ArrayList<>();

    @Column(name = "creado_por", length = 50)
    private String creadoPor;

    @Column(name = "actualizado_por", length = 50)
    private String actualizadoPor;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @PrePersist
    public void prePersist() {
        fechaCreacion = LocalDateTime.now();

        if (estado == null) {
            estado = EstadoCotizacion.BORRADOR;
        }
    }

    @PreUpdate
    public void preUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}