package com.runicsoft.bencolapp.clientes.repository;

import com.runicsoft.bencolapp.clientes.models.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    // validation methods
    boolean existsByTelefono(String telefono);
    boolean existsByEmail(String email);
    // validation methods at update
    boolean existsByTelefonoAndIdNot(String telefono, Long id);
    boolean existsByEmailAndIdNot(String email, Long id);

}
