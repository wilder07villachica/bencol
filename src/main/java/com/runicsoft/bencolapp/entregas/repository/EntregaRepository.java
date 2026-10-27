package com.runicsoft.bencolapp.entregas.repository;

import com.runicsoft.bencolapp.entregas.models.Entrega;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface EntregaRepository extends JpaRepository<Entrega, Long>, JpaSpecificationExecutor<Entrega> {
    Optional<Entrega> findByVentaId(Long ventaId);
}
