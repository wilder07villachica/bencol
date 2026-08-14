package com.runicsoft.bencolapp.cotizaciones.repository;

import com.runicsoft.bencolapp.cotizaciones.models.DetalleCotizacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DetalleCotizacionRepository extends JpaRepository<DetalleCotizacion, Long> {
    Optional<DetalleCotizacion> findByIdAndCotizacionId(Long id, Long cotizacionId);
}