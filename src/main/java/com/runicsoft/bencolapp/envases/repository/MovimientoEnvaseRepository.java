package com.runicsoft.bencolapp.envases.repository;

import com.runicsoft.bencolapp.envases.models.MovimientoEnvase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovimientoEnvaseRepository extends JpaRepository<MovimientoEnvase, Long> {

    List<MovimientoEnvase> findByCuentaEnvaseIdOrderByFechaMovimientoDesc(Long cuentaEnvaseId);
}