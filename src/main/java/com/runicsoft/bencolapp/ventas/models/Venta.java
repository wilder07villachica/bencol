package com.runicsoft.bencolapp.ventas.models;

import com.runicsoft.bencolapp.clientes.models.Cliente;
import com.runicsoft.bencolapp.ventas.utils.EstadoVenta;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "ventas",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "codigo")
        }
)
@Data
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String codigo;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @OneToMany(
            mappedBy = "venta",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<DetalleVenta> detalles = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoVenta estado;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @PrePersist
    public void prePersist() {
        fechaCreacion = LocalDateTime.now();
        if (estado == null) {
            estado = EstadoVenta.EMITIDA;
        }
    }

    @PreUpdate
    public void preUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}