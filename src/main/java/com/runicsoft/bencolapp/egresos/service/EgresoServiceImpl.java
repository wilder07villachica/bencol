package com.runicsoft.bencolapp.egresos.service;

import com.runicsoft.bencolapp.caja.models.Caja;
import com.runicsoft.bencolapp.caja.models.MovimientoCaja;
import com.runicsoft.bencolapp.caja.repository.CajaRepository;
import com.runicsoft.bencolapp.caja.repository.MovimientoCajaRepository;
import com.runicsoft.bencolapp.caja.utils.EstadoCaja;
import com.runicsoft.bencolapp.caja.utils.TipoMovimientoCaja;
import com.runicsoft.bencolapp.egresos.dtos.request.EgresoRequest;
import com.runicsoft.bencolapp.egresos.dtos.response.EgresoResponse;
import com.runicsoft.bencolapp.egresos.mapper.EgresoMapper;
import com.runicsoft.bencolapp.egresos.models.Egreso;
import com.runicsoft.bencolapp.egresos.repository.EgresoRepository;
import com.runicsoft.bencolapp.egresos.utils.CategoriaEgreso;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static com.runicsoft.bencolapp.utils.constants.MessageConstants.*;

@Service
@RequiredArgsConstructor
public class EgresoServiceImpl implements EgresoService {

    private final EgresoRepository egresoRepository;
    private final EgresoMapper egresoMapper;

    private final CajaRepository cajaRepository;
    private final MovimientoCajaRepository movimientoCajaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<EgresoResponse> findAll() {
        List<Egreso> egresos = egresoRepository.findAll();
        return egresoMapper.convertirListaEgresoDto(egresos);
    }

    @Override
    @Transactional(readOnly = true)
    public EgresoResponse findById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(ID_INVALIDO);
        }

        Egreso egreso = getEgreso(id);
        return egresoMapper.convertirEgresoDto(egreso);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EgresoResponse> findByCategoria(CategoriaEgreso categoria) {
        List<Egreso> egresos = egresoRepository.findByCategoria(categoria);
        return egresoMapper.convertirListaEgresoDto(egresos);
    }

    @Override
    @Transactional
    public EgresoResponse create(EgresoRequest request) {
        Caja caja = getCajaAbierta();
        validarSaldoCaja(caja, request.getMonto());

        Egreso egreso = egresoMapper.convertirEgresoEntidad(request);
        Egreso egresoGuardado = egresoRepository.save(egreso);
        registrarMovimientoCaja(caja, egresoGuardado);
        return egresoMapper.convertirEgresoDto(egresoGuardado);
    }

    // Métodos auxiliares
    private Egreso getEgreso(Long id) {
        return egresoRepository.findById(id)
                .orElseThrow(
                        () -> new IllegalArgumentException(EGRESO_NO_ENCONTRADO)
                );
    }

    private Caja getCajaAbierta() {
        return cajaRepository
                .findFirstByEstadoOrderByFechaAperturaDesc(EstadoCaja.ABIERTA)
                .orElseThrow(
                        () -> new IllegalArgumentException(CAJA_NO_ABIERTA)
                );
    }

    private void validarSaldoCaja(Caja caja, BigDecimal monto) {
        if (monto.compareTo(caja.getSaldoActual()) > 0) {
            throw new IllegalArgumentException(SALDO_CAJA_INSUFICIENTE);
        }
    }

    private void registrarMovimientoCaja(Caja caja, Egreso egreso) {
        BigDecimal nuevosEgresos = caja.getTotalEgresos().add(egreso.getMonto());
        BigDecimal nuevoSaldo = caja.getSaldoActual().subtract(egreso.getMonto());
        caja.setTotalEgresos(nuevosEgresos);
        caja.setSaldoActual(nuevoSaldo);
        MovimientoCaja movimiento = new MovimientoCaja();
        movimiento.setCaja(caja);
        movimiento.setTipoMovimiento(TipoMovimientoCaja.EGRESO);
        movimiento.setMonto(egreso.getMonto());
        movimiento.setConcepto(egreso.getConcepto());
        movimiento.setReferencia(egreso.getReferencia());
        MovimientoCaja movimientoGuardado = movimientoCajaRepository.save(movimiento);
        caja.getMovimientos().add(movimientoGuardado);
        cajaRepository.save(caja);
    }
}