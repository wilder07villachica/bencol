package com.runicsoft.bencolapp.caja.repository;

import com.runicsoft.bencolapp.caja.models.MovimientoCaja;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface MovimientoCajaRepository extends JpaRepository<MovimientoCaja, Long> {
    List<MovimientoCaja> findByCajaId(Long cajaId);

    List<MovimientoCaja> findByFechaMovimientoGreaterThanEqualAndFechaMovimientoLessThan(LocalDateTime desde, LocalDateTime hasta);
}