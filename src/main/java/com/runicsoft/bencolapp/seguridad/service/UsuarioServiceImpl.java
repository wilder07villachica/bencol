package com.runicsoft.bencolapp.seguridad.service;

import com.runicsoft.bencolapp.seguridad.dtos.request.CambiarPasswordRequest;
import com.runicsoft.bencolapp.seguridad.dtos.request.UsuarioRequest;
import com.runicsoft.bencolapp.seguridad.dtos.response.UsuarioResponse;
import com.runicsoft.bencolapp.seguridad.mapper.UsuarioMapper;
import com.runicsoft.bencolapp.seguridad.models.Usuario;
import com.runicsoft.bencolapp.seguridad.repository.UsuarioRepository;
import com.runicsoft.bencolapp.seguridad.utils.RolUsuario;
import com.runicsoft.bencolapp.utils.EstadoGeneral;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.runicsoft.bencolapp.utils.constants.MessageConstants.*;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponse> findAll() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        return usuarioMapper.convertirListaUsuarioDto(usuarios);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponse findById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(ID_INVALIDO);
        }
        Usuario usuario = getUsuario(id);
        return usuarioMapper.convertirUsuarioDto(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponse findByUsername(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException(USERNAME_INVALIDO);
        }
        Usuario usuario = usuarioRepository
                .findByUsername(username)
                .orElseThrow(
                        () -> new IllegalArgumentException(USUARIO_NO_ENCONTRADO)
                );
        return usuarioMapper.convertirUsuarioDto(usuario);
    }

    @Override
    @Transactional
    public UsuarioResponse create(UsuarioRequest request) {
        validarUsuarioDuplicado(request);
        Usuario usuario = usuarioMapper.convertirUsuarioEntidad(request);

        usuario.setPassword(passwordEncoder.encode(request.getPassword()));
        usuario.setEstado(EstadoGeneral.ACTIVO);
        Usuario usuarioGuardado = usuarioRepository.save(usuario);
        return usuarioMapper.convertirUsuarioDto(usuarioGuardado);
    }

    @Override
    @Transactional
    public UsuarioResponse cambiarRol(Long id, RolUsuario rol) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(ID_INVALIDO);
        }

        if (rol == null) {
            throw new IllegalArgumentException(ROL_INVALIDO);
        }

        Usuario usuario = getUsuario(id);
        usuario.setRol(rol);
        Usuario usuarioActualizado = usuarioRepository.save(usuario);
        return usuarioMapper.convertirUsuarioDto(usuarioActualizado);
    }

    @Override
    @Transactional
    public UsuarioResponse cambiarEstado(Long id, EstadoGeneral estado) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(ID_INVALIDO);
        }

        if (estado == null) {
            throw new IllegalArgumentException(ESTADO_USUARIO_INVALIDO);
        }

        Usuario usuario = getUsuario(id);
        usuario.setEstado(estado);
        Usuario usuarioActualizado = usuarioRepository.save(usuario);
        return usuarioMapper.convertirUsuarioDto(usuarioActualizado);
    }

    @Override
    @Transactional
    public void cambiarPassword(Long id, CambiarPasswordRequest request) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(ID_INVALIDO);
        }

        Usuario usuario = getUsuario(id);
        usuario.setPassword(passwordEncoder.encode(request.getNuevaPassword())
        );
        usuarioRepository.save(usuario);
    }

    // Métodos auxiliares
    private Usuario getUsuario(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(
                        () -> new IllegalArgumentException(USUARIO_NO_ENCONTRADO)
                );
    }

    private void validarUsuarioDuplicado(UsuarioRequest request) {
        if (usuarioRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException(USERNAME_EXISTENTE);
        }

        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(EMAIL_USUARIO_EXISTENTE);
        }
    }
}