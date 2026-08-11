package com.runicsoft.bencolapp.caja.repository;

import com.runicsoft.bencolapp.caja.models.Caja;
import com.runicsoft.bencolapp.caja.utils.EstadoCaja;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CajaRepository extends JpaRepository<Caja, Long> {
    boolean existsByEstado(EstadoCaja estado);
    Optional<Caja> findFirstByEstadoOrderByFechaAperturaDesc(EstadoCaja estado);
    List<Caja> findByEstado(EstadoCaja estado);

    @Query("""
        SELECT c
        FROM Caja c
        WHERE (:estado IS NULL OR c.estado = :estado)
          AND (:desde IS NULL OR c.fechaApertura >= :desde)
          AND (:hasta IS NULL OR c.fechaApertura < :hasta)
        """)
    Page<Caja> buscar(
            @Param("estado") EstadoCaja estado,
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta,
            Pageable pageable
    );
}