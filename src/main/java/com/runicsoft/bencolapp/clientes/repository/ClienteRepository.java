package com.runicsoft.bencolapp.clientes.repository;

import com.runicsoft.bencolapp.clientes.models.Cliente;
import com.runicsoft.bencolapp.clientes.utils.CategoriaCliente;
import com.runicsoft.bencolapp.utils.EstadoGeneral;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    boolean existsByTelefono(String telefono);

    boolean existsByEmail(String email);

    boolean existsByTelefonoAndIdNot(String telefono, Long id);

    boolean existsByEmailAndIdNot(String email, Long id);

    @Query("""
            SELECT c
            FROM Cliente c
            WHERE (:texto IS NULL
                   OR LOWER(c.nombre) LIKE LOWER(CONCAT('%', :texto, '%'))
                   OR LOWER(c.telefono) LIKE LOWER(CONCAT('%', :texto, '%'))
                   OR LOWER(c.email) LIKE LOWER(CONCAT('%', :texto, '%')))
              AND (:estado IS NULL OR c.estado = :estado)
              AND (:categoria IS NULL OR c.categoria = :categoria)
            """)
    Page<Cliente> buscar(
            @Param("texto") String texto,
            @Param("estado") EstadoGeneral estado,
            @Param("categoria") CategoriaCliente categoria,
            Pageable pageable
    );
}