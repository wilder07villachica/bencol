package com.runicsoft.bencolapp.productos.repository;

import com.runicsoft.bencolapp.productos.models.Producto;
import com.runicsoft.bencolapp.productos.utils.ProductoCategoria;
import com.runicsoft.bencolapp.utils.EstadoGeneral;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    Optional<Producto> findByCodigo(String codigo);
    boolean existsByCodigo(String codigo);
    boolean existsByCodigoAndIdNot(String codigo, Long id);

    @Query("""
        SELECT p
        FROM Producto p
        WHERE (:texto IS NULL
               OR LOWER(p.codigo) LIKE LOWER(CONCAT('%', :texto, '%'))
               OR LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :texto, '%')))
          AND (:estado IS NULL OR p.estado = :estado)
          AND (:categoria IS NULL OR p.categoria = :categoria)
        """)
    Page<Producto> buscar(
            @Param("texto") String texto,
            @Param("estado") EstadoGeneral estado,
            @Param("categoria") ProductoCategoria categoria,
            Pageable pageable
    );
}


