package com.runicsoft.bencolapp.clientes.repository;

import com.runicsoft.bencolapp.clientes.models.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}
