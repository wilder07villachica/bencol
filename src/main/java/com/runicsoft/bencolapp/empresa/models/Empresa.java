package com.runicsoft.bencolapp.empresa.models;

import com.runicsoft.bencolapp.utils.EstadoGeneral;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "empresas")
@Data
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 11)
    private String ruc;

    @Column(name = "razon_social", nullable = false, length = 225)
    private String razonSocial;

    @Column(name = "nombre_comercial", length = 225)
    private String nombreComercial;

    @Column(length = 255)
    private String direccion;

    @Column(length = 20)
    private String telefono;

    @Column(length = 100)
    private String email;

    @Column(name = "logo_nombre", length = 255)
    private String logoNombre;

    @Column(name = "logo_tipo", length = 100)
    private String logoTipo;

    @Column(name = "logo_ruta", length = 500)
    private String logoRuta;

    @Enumerated(EnumType.STRING)
    private EstadoGeneral estado;

    @PrePersist
    public void prePersist() {
        if(estado == null) {
            estado = EstadoGeneral.ACTIVO;
        }
    }
}