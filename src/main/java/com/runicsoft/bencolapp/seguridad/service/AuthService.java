package com.runicsoft.bencolapp.seguridad.service;

import com.runicsoft.bencolapp.seguridad.dtos.request.LoginRequest;
import com.runicsoft.bencolapp.seguridad.dtos.response.LoginResponse;
import com.runicsoft.bencolapp.seguridad.dtos.response.UsuarioAutenticadoResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request);
    UsuarioAutenticadoResponse obtenerUsuarioAutenticado(String username);
}