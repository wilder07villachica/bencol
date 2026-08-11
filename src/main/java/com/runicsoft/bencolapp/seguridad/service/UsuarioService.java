package com.runicsoft.bencolapp.seguridad.service;

import com.runicsoft.bencolapp.seguridad.dtos.request.CambiarPasswordRequest;
import com.runicsoft.bencolapp.seguridad.dtos.request.UsuarioRequest;
import com.runicsoft.bencolapp.seguridad.dtos.response.UsuarioResponse;
import com.runicsoft.bencolapp.seguridad.utils.RolUsuario;
import com.runicsoft.bencolapp.utils.EstadoGeneral;

import java.util.List;

public interface UsuarioService {
    List<UsuarioResponse> findAll();

    UsuarioResponse findById(Long id);

    UsuarioResponse findByUsername(String username);

    UsuarioResponse create(UsuarioRequest request);

    UsuarioResponse cambiarRol(Long id, RolUsuario rol);

    UsuarioResponse cambiarEstado(Long id, EstadoGeneral estado);

    void cambiarPassword(Long id, CambiarPasswordRequest request);
}