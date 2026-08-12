package com.runicsoft.bencolapp.envases.models;

import com.runicsoft.bencolapp.clientes.models.Cliente;
import com.runicsoft.bencolapp.productos.models.Producto;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "cuentas_envases_clientes",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_cuenta_envase_cliente_producto",
                        columnNames = {"cliente_id", "producto_id"}
                )
        }
)
@Data
public class CuentaEnvasesCliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @Column(name = "cantidad_propios", nullable = false)
    private Integer cantidadPropios;

    @Column(name = "cantidad_prestados", nullable = false)
    private Integer cantidadPrestados;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @PrePersist
    public void prePersist() {
        fechaCreacion = LocalDateTime.now();

        if (cantidadPropios == null) {
            cantidadPropios = 0;
        }

        if (cantidadPrestados == null) {
            cantidadPrestados = 0;
        }
    }

    @PreUpdate
    public void preUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}