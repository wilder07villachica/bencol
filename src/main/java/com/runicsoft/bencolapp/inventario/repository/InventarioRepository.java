package com.runicsoft.bencolapp.inventario.repository;

import com.runicsoft.bencolapp.inventario.models.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
}