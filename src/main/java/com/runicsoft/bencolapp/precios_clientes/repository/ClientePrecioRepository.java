package com.runicsoft.bencolapp.precios_clientes.repository;

import com.runicsoft.bencolapp.precios_clientes.models.ClientePrecio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ClientePrecioRepository extends JpaRepository<ClientePrecio, Long> {
    boolean existsByClienteIdAndProductoId(Long clienteId, Long productoId);
    boolean existsByClienteIdAndProductoIdAndIdNot(Long clienteId, Long productoId, Long id);

    Optional<ClientePrecio> findByClienteIdAndProductoId(Long clienteId, Long productoId);

    @Query("""
        SELECT cp
        FROM ClientePrecio cp
        WHERE (:clienteId IS NULL OR cp.cliente.id = :clienteId)
          AND (:productoId IS NULL OR cp.producto.id = :productoId)
        """)
    Page<ClientePrecio> buscar(
            @Param("clienteId") Long clienteId,
            @Param("productoId") Long productoId,
            Pageable pageable
    );
}
