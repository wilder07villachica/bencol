package com.runicsoft.bencolapp.empresa.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(
        name = "empresas",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "ruc")
        }
)
@Data
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ruc;

    @Column(name = "razon_social", length = 225)
    private String razonSocial;

    @Column(name = "nombre_comercial", length = 225)
    private String nombreComercial;

    private String estado;

    @PrePersist
    public void prePersist() {
        if(estado == null) {
            estado = "Activo";
        }
    }
}
