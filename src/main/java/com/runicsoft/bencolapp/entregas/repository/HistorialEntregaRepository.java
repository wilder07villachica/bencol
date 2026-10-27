package com.runicsoft.bencolapp.entregas.repository;

import com.runicsoft.bencolapp.entregas.models.HistorialEntrega;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistorialEntregaRepository extends JpaRepository<HistorialEntrega, Long> {
    Page<HistorialEntrega> findByEntregaId(Long entregaId, Pageable pageable);
}
