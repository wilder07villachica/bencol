package com.runicsoft.bencolapp.seguridad.controller;

import com.runicsoft.bencolapp.seguridad.dtos.request.LoginRequest;
import com.runicsoft.bencolapp.seguridad.dtos.response.LoginResponse;
import com.runicsoft.bencolapp.seguridad.dtos.response.UsuarioAutenticadoResponse;
import com.runicsoft.bencolapp.seguridad.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bencol.agua/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioAutenticadoResponse> me(Authentication authentication) {
        return ResponseEntity.ok(authService.obtenerUsuarioAutenticado(authentication.getName()));
    }
}