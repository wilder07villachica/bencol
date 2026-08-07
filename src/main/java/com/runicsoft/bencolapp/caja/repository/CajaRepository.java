package com.runicsoft.bencolapp.caja.repository;

import com.runicsoft.bencolapp.caja.models.Caja;
import com.runicsoft.bencolapp.caja.utils.EstadoCaja;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CajaRepository extends JpaRepository<Caja, Long> {
    boolean existsByEstado(EstadoCaja estado);
    Optional<Caja> findFirstByEstadoOrderByFechaAperturaDesc(EstadoCaja estado);
    List<Caja> findByEstado(EstadoCaja estado);
}