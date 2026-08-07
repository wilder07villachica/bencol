package com.runicsoft.bencolapp.caja.service;

import com.runicsoft.bencolapp.caja.dtos.request.CajaRequest;
import com.runicsoft.bencolapp.caja.dtos.request.MovimientoCajaRequest;
import com.runicsoft.bencolapp.caja.dtos.response.CajaResponse;
import com.runicsoft.bencolapp.caja.dtos.response.MovimientoCajaResponse;
import com.runicsoft.bencolapp.caja.mapper.CajaMapper;
import com.runicsoft.bencolapp.caja.mapper.MovimientoCajaMapper;
import com.runicsoft.bencolapp.caja.models.Caja;
import com.runicsoft.bencolapp.caja.models.MovimientoCaja;
import com.runicsoft.bencolapp.caja.repository.CajaRepository;
import com.runicsoft.bencolapp.caja.repository.MovimientoCajaRepository;
import com.runicsoft.bencolapp.caja.utils.EstadoCaja;
import com.runicsoft.bencolapp.caja.utils.TipoMovimientoCaja;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static com.runicsoft.bencolapp.utils.constants.MessageConstants.*;

@Service
@RequiredArgsConstructor
public class CajaServiceImpl implements CajaService {

    private final CajaRepository cajaRepository;
    private final MovimientoCajaRepository movimientoCajaRepository;
    private final CajaMapper cajaMapper;
    private final MovimientoCajaMapper movimientoCajaMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CajaResponse> findAll() {
        List<Caja> cajas = cajaRepository.findAll();
        return cajaMapper.convertirListaCajaDto(cajas);
    }

    @Override
    @Transactional(readOnly = true)
    public CajaResponse findById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(ID_INVALIDO);
        }
        Caja caja = getCaja(id);
        return cajaMapper.convertirCajaDto(caja);
    }

    @Override
    @Transactional(readOnly = true)
    public CajaResponse findCajaAbierta() {
        Caja caja = getCajaAbierta();
        return cajaMapper.convertirCajaDto(caja);
    }

    @Override
    @Transactional
    public CajaResponse abrirCaja(CajaRequest request) {
        if (cajaRepository.existsByEstado(EstadoCaja.ABIERTA)) {
            throw new IllegalArgumentException(CAJA_YA_ABIERTA);
        }

        Caja caja = new Caja();
        caja.setSaldoInicial(request.getSaldoInicial());
        caja.setTotalIngresos(BigDecimal.ZERO);
        caja.setTotalEgresos(BigDecimal.ZERO);
        caja.setSaldoActual(request.getSaldoInicial());
        caja.setEstado(EstadoCaja.ABIERTA);
        Caja cajaGuardada = cajaRepository.save(caja);
        return cajaMapper.convertirCajaDto(cajaGuardada);
    }

    @Override
    @Transactional
    public MovimientoCajaResponse registrarMovimiento(MovimientoCajaRequest request) {
        Caja caja = getCaja(request.getCajaId());
        validarCajaAbierta(caja);

        if (request.getTipoMovimiento() == TipoMovimientoCaja.INGRESO) {
            registrarIngreso(caja, request.getMonto());
        } else if (request.getTipoMovimiento() == TipoMovimientoCaja.EGRESO) {
            registrarEgreso(caja, request.getMonto());
        } else {
            throw new IllegalArgumentException(TIPO_MOVIMIENTO_CAJA_INVALIDO);
        }

        MovimientoCaja movimiento = new MovimientoCaja();
        movimiento.setCaja(caja);
        movimiento.setTipoMovimiento(request.getTipoMovimiento());
        movimiento.setMonto(request.getMonto());
        movimiento.setConcepto(request.getConcepto());
        movimiento.setReferencia(request.getReferencia());

        MovimientoCaja movimientoGuardado = movimientoCajaRepository.save(movimiento);
        caja.getMovimientos().add(movimientoGuardado);
        cajaRepository.save(caja);
        return movimientoCajaMapper.convertirMovimientoDto(movimientoGuardado);
    }

    @Override
    @Transactional
    public CajaResponse cerrarCaja(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(ID_INVALIDO);
        }
        Caja caja = getCaja(id);
        validarCajaAbierta(caja);
        caja.setEstado(EstadoCaja.CERRADA);
        caja.setFechaCierre(LocalDateTime.now());
        Caja cajaCerrada = cajaRepository.save(caja);
        return cajaMapper.convertirCajaDto(cajaCerrada);
    }

    // Métodos auxiliares

    private Caja getCaja(Long id) {
        return cajaRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException(CAJA_NO_ENCONTRADA)
        );
    }

    private Caja getCajaAbierta() {
        return cajaRepository.findFirstByEstadoOrderByFechaAperturaDesc(EstadoCaja.ABIERTA)
                .orElseThrow(
                        () -> new IllegalArgumentException(CAJA_NO_ABIERTA)
                );
    }

    private void validarCajaAbierta(Caja caja) {
        if (caja.getEstado() != EstadoCaja.ABIERTA) {
            throw new IllegalArgumentException(CAJA_CERRADA);
        }
    }

    private void registrarIngreso(Caja caja, BigDecimal monto) {
        BigDecimal nuevosIngresos = caja.getTotalIngresos().add(monto);
        BigDecimal nuevoSaldo = caja.getSaldoActual().add(monto);
        caja.setTotalIngresos(nuevosIngresos);
        caja.setSaldoActual(nuevoSaldo);
    }

    private void registrarEgreso(Caja caja, BigDecimal monto) {
        if (monto.compareTo(caja.getSaldoActual()) > 0) {
            throw new IllegalArgumentException(SALDO_CAJA_INSUFICIENTE);
        }
        BigDecimal nuevosEgresos = caja.getTotalEgresos().add(monto);
        BigDecimal nuevoSaldo = caja.getSaldoActual().subtract(monto);
        caja.setTotalEgresos(nuevosEgresos);
        caja.setSaldoActual(nuevoSaldo);
    }
}