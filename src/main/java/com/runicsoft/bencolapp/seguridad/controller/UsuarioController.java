package com.runicsoft.bencolapp.seguridad.controller;

import com.runicsoft.bencolapp.seguridad.dtos.request.CambiarPasswordRequest;
import com.runicsoft.bencolapp.seguridad.dtos.request.UsuarioRequest;
import com.runicsoft.bencolapp.seguridad.dtos.response.UsuarioResponse;
import com.runicsoft.bencolapp.seguridad.service.UsuarioService;
import com.runicsoft.bencolapp.seguridad.utils.RolUsuario;
import com.runicsoft.bencolapp.utils.EstadoGeneral;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bencol.agua/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> findAll() {
        return ResponseEntity.ok(usuarioService.findAll());
    }

    @GetMapping("/{idUsuario}")
    public ResponseEntity<UsuarioResponse> findById(@PathVariable Long idUsuario) {
        return ResponseEntity.ok(usuarioService.findById(idUsuario));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UsuarioResponse> findByUsername(@PathVariable String username) {
        return ResponseEntity.ok(usuarioService.findByUsername(username));
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> create(@Valid @RequestBody UsuarioRequest request) {
        UsuarioResponse response = usuarioService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{idUsuario}/rol")
    public ResponseEntity<UsuarioResponse> cambiarRol(@PathVariable Long idUsuario, @RequestParam RolUsuario rol) {
        return ResponseEntity.ok(usuarioService.cambiarRol(idUsuario, rol));
    }

    @PatchMapping("/{idUsuario}/estado")
    public ResponseEntity<UsuarioResponse> cambiarEstado(@PathVariable Long idUsuario, @RequestParam EstadoGeneral estado) {
        return ResponseEntity.ok(usuarioService.cambiarEstado(idUsuario, estado));
    }

    @PatchMapping("/{idUsuario}/password")
    public ResponseEntity<Void> cambiarPassword(@PathVariable Long idUsuario, @Valid @RequestBody CambiarPasswordRequest request) {
        usuarioService.cambiarPassword(idUsuario, request);
        return ResponseEntity.noContent().build();
    }
}