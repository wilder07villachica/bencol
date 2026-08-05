package com.runicsoft.bencolapp.clientes.models;

import com.runicsoft.bencolapp.clientes.utils.CategoriaCliente;
import com.runicsoft.bencolapp.utils.EstadoGeneral;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "clients")
@Data
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String direccion;

    @Column(nullable = false, unique = true)
    private String telefono;

    @Column(nullable = false, unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    private CategoriaCliente categoria;

    @Enumerated(EnumType.STRING)
    private EstadoGeneral estado;

    @PrePersist
    public void prePersist() {
        if (this.estado == null) {
            this.estado = EstadoGeneral.ACTIVO;
        }
        if (this.categoria == null) {
            this.categoria = CategoriaCliente.CONSUMIDOR_FINAL;
        }
    }
}
