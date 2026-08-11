package com.runicsoft.bencolapp.seguridad.dtos.response;

import com.runicsoft.bencolapp.seguridad.utils.RolUsuario;
import lombok.Data;

@Data
public class UsuarioAutenticadoResponse {
    private Long usuarioId;
    private String username;
    private String email;
    private RolUsuario rol;
}