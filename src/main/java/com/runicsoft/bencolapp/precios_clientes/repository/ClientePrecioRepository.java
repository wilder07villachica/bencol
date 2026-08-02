package com.runicsoft.bencolapp.precios_clientes.repository;

import com.runicsoft.bencolapp.precios_clientes.models.ClientePrecio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClientePrecioRepository extends JpaRepository<ClientePrecio, Long> {
    Optional<ClientePrecio> findByClienteIdAndProductoId(Long clientId, Long productId);
    List<ClientePrecio> findByClienteId(Long clientId);
    List<ClientePrecio> findByProductoId(Long productId);
}
