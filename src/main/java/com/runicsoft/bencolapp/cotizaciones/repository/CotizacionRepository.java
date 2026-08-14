package com.runicsoft.bencolapp.cotizaciones.repository;

import com.runicsoft.bencolapp.cotizaciones.models.Cotizacion;
import com.runicsoft.bencolapp.cotizaciones.utils.EstadoCotizacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface CotizacionRepository extends JpaRepository<Cotizacion, Long> {

    Optional<Cotizacion> findByCodigo(String codigo);

    @Query("""
            SELECT c
            FROM Cotizacion c
            WHERE (:codigo IS NULL OR LOWER(c.codigo) LIKE LOWER(CONCAT('%', :codigo, '%')))
            AND (:clienteId IS NULL OR c.cliente.id = :clienteId)
            AND (:estado IS NULL OR c.estado = :estado)
            AND (:desde IS NULL OR c.fechaCreacion >= :desde)
            AND (:hasta IS NULL OR c.fechaCreacion < :hasta)
            """)
    Page<Cotizacion> buscar(
            @Param("codigo") String codigo,
            @Param("clienteId") Long clienteId,
            @Param("estado") EstadoCotizacion estado,
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta,
            Pageable pageable
    );
}