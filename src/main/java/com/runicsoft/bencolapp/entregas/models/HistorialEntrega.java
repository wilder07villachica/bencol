package com.runicsoft.bencolapp.entregas.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "historial_entregas")
@Getter
@Setter
public class HistorialEntrega {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "entrega_id",
            nullable = false
    )
    private Entrega entrega;

    @Column(nullable = false, length = 30)
    private String accion;

    @Column(length = 20)
    private String estadoAnterior;

    @Column(nullable = false, length = 20)
    private String estadoNuevo;

    @Column(length = 1000)
    private String observaciones;

    @Column(nullable = false, length = 50)
    private String usuario;

    @Column(nullable = false)
    private LocalDateTime fecha;
}