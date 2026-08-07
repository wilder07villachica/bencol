package com.runicsoft.bencolapp.egresos.repository;

import com.runicsoft.bencolapp.egresos.models.Egreso;
import com.runicsoft.bencolapp.egresos.utils.CategoriaEgreso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EgresoRepository extends JpaRepository<Egreso, Long> {
    List<Egreso> findByCategoria(CategoriaEgreso categoria);
}