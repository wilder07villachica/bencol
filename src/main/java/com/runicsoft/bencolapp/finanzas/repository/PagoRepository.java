package com.runicsoft.bencolapp.finanzas.repository;

import com.runicsoft.bencolapp.finanzas.models.Pago;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PagoRepository extends JpaRepository<Pago, Long> {
    List<Pago> findByCuentaCobrarId(Long cuentaCobrarId);
}