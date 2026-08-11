package com.runicsoft.bencolapp.egresos.service;

import com.runicsoft.bencolapp.caja.service.CajaService;
import com.runicsoft.bencolapp.egresos.dtos.request.EgresoRequest;
import com.runicsoft.bencolapp.egresos.dtos.response.EgresoResponse;
import com.runicsoft.bencolapp.egresos.mapper.EgresoMapper;
import com.runicsoft.bencolapp.egresos.models.Egreso;
import com.runicsoft.bencolapp.egresos.repository.EgresoRepository;
import com.runicsoft.bencolapp.egresos.utils.CategoriaEgreso;
import com.runicsoft.bencolapp.seguridad.utils.SecurityUtils;
import com.runicsoft.bencolapp.utils.exceptions.ResourceNotFoundException;
import com.runicsoft.bencolapp.utils.pagination.PaginaResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    public PaginaResponse<EgresoResponse> findAll(int pagina, int tamanio, CategoriaEgreso categoria, LocalDate desde, LocalDate hasta) {
        validarPaginacion(pagina, tamanio);
        validarRangoFechas(desde, hasta);

        LocalDateTime fechaInicio = desde != null ? desde.atStartOfDay() : null;
        LocalDateTime fechaFin = hasta != null ? hasta.plusDays(1).atStartOfDay() : null;

        Pageable pageable = PageRequest.of(
                pagina,
                tamanio,
                Sort.by("fechaEgreso").descending()
        );

        Page<Egreso> egresos = egresoRepository.buscar(
                categoria,
                fechaInicio,
                fechaFin,
                pageable
        );

        Page<EgresoResponse> responses = egresos.map(egresoMapper::convertirEgresoDto);
        return PaginaResponse.from(responses);
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
        if (categoria == null) {
            throw new IllegalArgumentException(CATEGORIA_EGRESO_INVALIDA);
        }

        List<Egreso> egresos = egresoRepository.findByCategoria(categoria);
        return egresoMapper.convertirListaEgresoDto(egresos);
    }

    @Override
    @Transactional
    public EgresoResponse create(EgresoRequest request) {
        Egreso egreso = egresoMapper.convertirEgresoEntidad(request);
        egreso.setRegistradoPor(SecurityUtils.getUsuarioActual());

        Egreso egresoGuardado = egresoRepository.save(egreso);

        cajaService.registrarEgreso(
                egresoGuardado.getMonto(),
                egresoGuardado.getConcepto(),
                egresoGuardado.getReferencia()
        );

        return egresoMapper.convertirEgresoDto(egresoGuardado);
    }

    private Egreso getEgreso(Long id) {
        return egresoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(EGRESO_NO_ENCONTRADO));
    }

    private void validarPaginacion(int pagina, int tamanio) {
        if (pagina < 0) {
            throw new IllegalArgumentException(PAGINA_INVALIDA);
        }

        if (tamanio <= 0 || tamanio > 100) {
            throw new IllegalArgumentException(TAMANIO_PAGINA_INVALIDO);
        }
    }

    private void validarRangoFechas(LocalDate desde, LocalDate hasta) {
        if (desde != null && hasta != null && desde.isAfter(hasta)) {
            throw new IllegalArgumentException(RANGO_FECHAS_INVALIDO);
        }
    }
}