package com.runicsoft.bencolapp.entregas.models;

import com.runicsoft.bencolapp.entregas.utils.EstadoEntrega;
import com.runicsoft.bencolapp.seguridad.models.Usuario;
import com.runicsoft.bencolapp.ventas.models.Venta;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "entregas")
@Getter
@Setter
public class Entrega {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "venta_id",
            nullable = false,
            unique = true
    )
    private Venta venta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "repartidor_id")
    private Usuario repartidor;

    @Column(nullable = false)
    private String direccion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoEntrega estado;

    @Column(length = 1000)
    private String observaciones;

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(nullable = false)
    private LocalDateTime fechaActualizacion;

    private LocalDateTime fechaProgramada;

    private LocalDateTime fechaEntrega;

    @Version
    @Column(nullable = false)
    private Long version;
}