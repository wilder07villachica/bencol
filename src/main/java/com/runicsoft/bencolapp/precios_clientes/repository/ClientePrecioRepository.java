package com.runicsoft.bencolapp.precios_clientes.repository;

import com.runicsoft.bencolapp.precios_clientes.models.ClientePrecio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientePrecioRepository extends JpaRepository<ClientePrecio, Long> {
    boolean existsByClienteIdAndProductoId(Long clienteId, Long productoId);
    boolean existsByClienteIdAndProductoIdAndIdNot(Long clienteId, Long productoId, Long id);

    Optional<ClientePrecio> findByClienteIdAndProductoId(Long clienteId, Long productoId);
}
