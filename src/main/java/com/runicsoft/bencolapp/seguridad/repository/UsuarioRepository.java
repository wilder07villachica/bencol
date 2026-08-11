package com.runicsoft.bencolapp.seguridad.repository;

import com.runicsoft.bencolapp.seguridad.models.Usuario;
import com.runicsoft.bencolapp.seguridad.utils.RolUsuario;
import com.runicsoft.bencolapp.utils.EstadoGeneral;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsername(String username);

    Optional<Usuario> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsernameAndIdNot(String username, Long id);

    boolean existsByEmailAndIdNot(String email, Long id);

    @Query("""
        SELECT u
        FROM Usuario u
        WHERE (:texto IS NULL
               OR LOWER(u.username) LIKE LOWER(CONCAT('%', :texto, '%'))
               OR LOWER(u.email) LIKE LOWER(CONCAT('%', :texto, '%')))
          AND (:rol IS NULL OR u.rol = :rol)
          AND (:estado IS NULL OR u.estado = :estado)
        """)
    Page<Usuario> buscar(
            @Param("texto") String texto,
            @Param("rol") RolUsuario rol,
            @Param("estado") EstadoGeneral estado,
            Pageable pageable
    );
}