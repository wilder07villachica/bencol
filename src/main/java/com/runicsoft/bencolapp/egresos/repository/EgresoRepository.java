package com.runicsoft.bencolapp.egresos.repository;

import com.runicsoft.bencolapp.egresos.models.Egreso;
import com.runicsoft.bencolapp.egresos.utils.CategoriaEgreso;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface EgresoRepository extends JpaRepository<Egreso, Long> {
    List<Egreso> findByCategoria(CategoriaEgreso categoria);

    @Query("""
        SELECT e
        FROM Egreso e
        WHERE (:categoria IS NULL OR e.categoria = :categoria)
          AND (:desde IS NULL OR e.fechaEgreso >= :desde)
          AND (:hasta IS NULL OR e.fechaEgreso < :hasta)
        """)
    Page<Egreso> buscar(
            @Param("categoria") CategoriaEgreso categoria,
            @Param("desde") LocalDateTime desde,
            @Param("hasta") LocalDateTime hasta,
            Pageable pageable
    );
}