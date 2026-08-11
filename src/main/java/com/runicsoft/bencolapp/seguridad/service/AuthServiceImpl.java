package com.runicsoft.bencolapp.seguridad.service;

import com.runicsoft.bencolapp.seguridad.dtos.request.LoginRequest;
import com.runicsoft.bencolapp.seguridad.dtos.response.LoginResponse;
import com.runicsoft.bencolapp.seguridad.dtos.response.UsuarioAutenticadoResponse;
import com.runicsoft.bencolapp.seguridad.models.Usuario;
import com.runicsoft.bencolapp.seguridad.repository.UsuarioRepository;
import com.runicsoft.bencolapp.utils.EstadoGeneral;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository
                .findByUsername(request.getUsername())
                .orElseThrow(
                        () -> new IllegalArgumentException("Credenciales incorrectas.")
                );

        if (usuario.getEstado() != EstadoGeneral.ACTIVO) {
            throw new IllegalArgumentException("El usuario se encuentra inactivo.");
        }
        if (!passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
            throw new IllegalArgumentException("Credenciales incorrectas.");
        }

        String token = jwtService.generarToken(usuario);
        LoginResponse response = new LoginResponse();
        response.setUsuarioId(usuario.getId());
        response.setUsername(usuario.getUsername());
        response.setEmail(usuario.getEmail());
        response.setRol(usuario.getRol());
        response.setToken(token);
        response.setTipoToken("Bearer");

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioAutenticadoResponse obtenerUsuarioAutenticado(String username) {
        Usuario usuario = usuarioRepository
                .findByUsername(username)
                .orElseThrow(
                        () -> new IllegalArgumentException("Usuario autenticado no encontrado.")
                );

        UsuarioAutenticadoResponse response = new UsuarioAutenticadoResponse();
        response.setUsuarioId(usuario.getId());
        response.setUsername(usuario.getUsername());
        response.setEmail(usuario.getEmail());
        response.setRol(usuario.getRol());

        return response;
    }
}