package com.runicsoft.bencolapp.empresa.repository;

import com.runicsoft.bencolapp.empresa.models.Empresa;
import com.runicsoft.bencolapp.utils.EstadoGeneral;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
    Boolean existsByRuc(String ruc);
    Boolean existsByRucAndIdNot(String ruc, Long id);
    Optional<Empresa> findFirstByEstadoOrderByIdAsc(EstadoGeneral estado);
}