package com.runicsoft.bencolapp.productos.models;

import com.runicsoft.bencolapp.productos.utils.ProductoCategoria;
import com.runicsoft.bencolapp.productos.utils.UnidadMedida;
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

    @Column(nullable = false, unique = true, length = 20)
    private String codigo;

    @Column(nullable = false, length = 255)
    private String descripcion;

    @Column(name = "unidades_por_paquete", nullable = false)
    private Integer unidadesPorPaquete;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal contenido;

    @Enumerated(EnumType.STRING)
    @Column(name = "unidad_medida", nullable = false)
    private UnidadMedida unidadMedida;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductoCategoria categoria;

    @Column(name = "precio_base", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioBase;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoGeneral estado;

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
