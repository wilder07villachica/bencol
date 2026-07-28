package com.runicsoft.bencolapp.empresa.repository;

import com.runicsoft.bencolapp.empresa.models.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
    Boolean existsByRuc(String ruc);
}
