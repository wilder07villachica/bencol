package com.runicsoft.bencolapp.seguridad.dtos.response;

import com.runicsoft.bencolapp.seguridad.utils.RolUsuario;
import com.runicsoft.bencolapp.utils.EstadoGeneral;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UsuarioResponse {
    private Long id;
    private String username;
    private String email;
    private RolUsuario rol;
    private EstadoGeneral estado;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}