package com.runicsoft.bencolapp.egresos.service;

import com.runicsoft.bencolapp.caja.service.CajaService;
import com.runicsoft.bencolapp.egresos.dtos.request.EgresoRequest;
import com.runicsoft.bencolapp.egresos.dtos.response.EgresoResponse;
import com.runicsoft.bencolapp.egresos.mapper.EgresoMapper;
import com.runicsoft.bencolapp.egresos.models.Egreso;
import com.runicsoft.bencolapp.egresos.repository.EgresoRepository;
import com.runicsoft.bencolapp.egresos.utils.CategoriaEgreso;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.runicsoft.bencolapp.utils.constants.MessageConstants.*;

@Service
@RequiredArgsConstructor
public class EgresoServiceImpl implements EgresoService {

    private final EgresoRepository egresoRepository;
    private final EgresoMapper egresoMapper;

    private final CajaService cajaService;

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
        Egreso egreso = egresoMapper.convertirEgresoEntidad(request);
        Egreso egresoGuardado = egresoRepository.save(egreso);
        cajaService.registrarEgreso(
                egresoGuardado.getMonto(),
                egresoGuardado.getConcepto(),
                egresoGuardado.getReferencia()
        );
        return egresoMapper.convertirEgresoDto(egresoGuardado);
    }

    // Métodos auxiliares
    private Egreso getEgreso(Long id) {
        return egresoRepository.findById(id)
                .orElseThrow(
                        () -> new IllegalArgumentException(EGRESO_NO_ENCONTRADO)
                );
    }
}