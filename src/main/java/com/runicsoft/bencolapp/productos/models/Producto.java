package com.runicsoft.bencolapp.productos.models;

import com.runicsoft.bencolapp.productos.utils.ProductoCategoria;
import com.runicsoft.bencolapp.utils.EstadoGeneral;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Table(
        name = "products",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "codigo")
        }
)
@Data
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String codigo;

    private String descripcion;

    @Enumerated(EnumType.STRING)
    private ProductoCategoria categoria;

    @Column(name = "precio_base")
    private BigDecimal precioBase;

    @Enumerated(EnumType.STRING)
    private EstadoGeneral  estado;

    @PrePersist
    public void prePersist() {
        if (categoria == null) {
            categoria = ProductoCategoria.BIDON;
        }
        if (estado == null) {
            estado = EstadoGeneral.ACTIVO;
        }
    }
}
