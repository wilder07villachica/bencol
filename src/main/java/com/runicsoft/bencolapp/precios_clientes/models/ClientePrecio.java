package com.runicsoft.bencolapp.precios_clientes.models;

import com.runicsoft.bencolapp.clientes.models.Cliente;
import com.runicsoft.bencolapp.productos.models.Producto;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "clientes_precios",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"cliente_id", "producto_id"})
        }
)
@Data
public class ClientePrecio {

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "producto_id")
    private Producto producto;

    private BigDecimal precio;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    @PrePersist
    public void prePersist() {
        fechaCreacion = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}
