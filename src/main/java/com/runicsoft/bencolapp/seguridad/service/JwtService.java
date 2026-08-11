package com.runicsoft.bencolapp.seguridad.service;

import com.runicsoft.bencolapp.seguridad.models.Usuario;

public interface JwtService {
    String generarToken(Usuario usuario);
}