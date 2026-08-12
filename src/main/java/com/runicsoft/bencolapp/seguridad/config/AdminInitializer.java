package com.runicsoft.bencolapp.seguridad.config;

import com.runicsoft.bencolapp.seguridad.models.Usuario;
import com.runicsoft.bencolapp.seguridad.repository.UsuarioRepository;
import com.runicsoft.bencolapp.seguridad.utils.RolUsuario;
import com.runicsoft.bencolapp.utils.EstadoGeneral;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class AdminInitializer {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${security.admin.username:}")
    private String username;

    @Value("${security.admin.email:}")
    private String email;

    @Value("${security.admin.password:}")
    private String password;

    @Bean
    public CommandLineRunner crearAdministradorInicial() {
        return args -> {
            if (usuarioRepository.count() > 0) {
                return;
            }

            if (username.isBlank() || email.isBlank() || password.isBlank()) {
                log.warn("No se creó el administrador inicial porque faltan las propiedades security.admin.");
                return;
            }

            Usuario usuario = new Usuario();
            usuario.setUsername(username);
            usuario.setEmail(email);
            usuario.setPassword(passwordEncoder.encode(password));
            usuario.setRol(RolUsuario.ADMIN);
            usuario.setEstado(EstadoGeneral.ACTIVO);

            Usuario usuarioGuardado = usuarioRepository.save(usuario);

            log.info(
                    "Administrador inicial creado. id={}, username={}",
                    usuarioGuardado.getId(),
                    usuarioGuardado.getUsername()
            );
        };
    }
}