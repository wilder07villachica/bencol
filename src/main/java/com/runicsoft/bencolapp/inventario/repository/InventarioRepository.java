package com.runicsoft.bencolapp.inventario.repository;

import com.runicsoft.bencolapp.inventario.models.Inventario;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface InventarioRepository extends JpaRepository<Inventario, Long> {
    boolean existsByProductoId(Long productoId);

    Optional<Inventario> findByProductoId(Long productoId);

    // ===========================================================================================================
    @Query("""
                SELECT COUNT(i)
                FROM Inventario i
            """)
    Long countInventarios();

    @Query("""
                SELECT COUNT(i)
                FROM Inventario i
                WHERE i.stockActual <= i.stockMinimo
            """)
    Long countStockBajo();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT i
        FROM Inventario i
        WHERE i.producto.id = :productoId
        """)
    Optional<Inventario> findByProductoIdForUpdate(
            @Param("productoId") Long productoId
    );
}