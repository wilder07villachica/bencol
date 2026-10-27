package com.runicsoft.bencolapp.entregas.dtos.response;

import com.runicsoft.bencolapp.seguridad.utils.RolUsuario;
import lombok.Data;

@Data
public class ResponsableEntregaResponse {

    private Long id;
    private String username;
    private RolUsuario rol;
}
