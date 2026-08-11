package com.runicsoft.bencolapp.proveedores.repository;

import com.runicsoft.bencolapp.proveedores.models.Proveedor;
import com.runicsoft.bencolapp.utils.EstadoGeneral;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
    boolean existsByRuc(String ruc);
    boolean existsByRucAndIdNot(String ruc, Long id);
    Optional<Proveedor> findByRuc(String ruc);

    @Query("""
        SELECT p
        FROM Proveedor p
        WHERE (:texto IS NULL
               OR LOWER(p.ruc) LIKE LOWER(CONCAT('%', :texto, '%'))
               OR LOWER(p.razonSocial) LIKE LOWER(CONCAT('%', :texto, '%')))
          AND (:estado IS NULL OR p.estado = :estado)
        """)
    Page<Proveedor> buscar(
            @Param("texto") String texto,
            @Param("estado") EstadoGeneral estado,
            Pageable pageable
    );
}