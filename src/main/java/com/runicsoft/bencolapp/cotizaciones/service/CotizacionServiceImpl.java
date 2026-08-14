package com.runicsoft.bencolapp.cotizaciones.service;

import com.runicsoft.bencolapp.clientes.models.Cliente;
import com.runicsoft.bencolapp.clientes.repository.ClienteRepository;
import com.runicsoft.bencolapp.cotizaciones.dtos.request.CotizacionRequest;
import com.runicsoft.bencolapp.cotizaciones.dtos.request.DetalleCotizacionRequest;
import com.runicsoft.bencolapp.cotizaciones.dtos.response.CotizacionResponse;
import com.runicsoft.bencolapp.cotizaciones.mapper.CotizacionMapper;
import com.runicsoft.bencolapp.cotizaciones.models.Cotizacion;
import com.runicsoft.bencolapp.cotizaciones.models.DetalleCotizacion;
import com.runicsoft.bencolapp.cotizaciones.repository.CotizacionRepository;
import com.runicsoft.bencolapp.cotizaciones.utils.EstadoCotizacion;
import com.runicsoft.bencolapp.empresa.models.Empresa;
import com.runicsoft.bencolapp.empresa.repository.EmpresaRepository;
import com.runicsoft.bencolapp.envases.utils.TipoMovimientoEnvase;
import com.runicsoft.bencolapp.precios_clientes.models.ClientePrecio;
import com.runicsoft.bencolapp.precios_clientes.repository.ClientePrecioRepository;
import com.runicsoft.bencolapp.productos.models.Producto;
import com.runicsoft.bencolapp.productos.repository.ProductoRepository;
import com.runicsoft.bencolapp.productos.utils.ProductoCategoria;
import com.runicsoft.bencolapp.seguridad.utils.SecurityUtils;
import com.runicsoft.bencolapp.utils.EstadoGeneral;
import com.runicsoft.bencolapp.utils.exceptions.BusinessException;
import com.runicsoft.bencolapp.utils.exceptions.ResourceNotFoundException;
import com.runicsoft.bencolapp.utils.pagination.PaginaResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.runicsoft.bencolapp.utils.constants.MessageConstants.*;

@Service
@RequiredArgsConstructor
public class CotizacionServiceImpl implements CotizacionService {

    private final CotizacionRepository cotizacionRepository;
    private final EmpresaRepository empresaRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final ClientePrecioRepository clientePrecioRepository;
    private final CotizacionMapper cotizacionMapper;

    @Override
    @Transactional(readOnly = true)
    public PaginaResponse<CotizacionResponse> findAll(int pagina, int tamanio, String codigo, Long clienteId, EstadoCotizacion estado, LocalDate desde, LocalDate hasta) {
        validarPaginacion(pagina, tamanio);
        validarRangoFechas(desde, hasta);

        if (codigo != null && codigo.isBlank()) {
            codigo = null;
        }

        if (clienteId != null) {
            if (clienteId <= 0) {
                throw new IllegalArgumentException(ID_INVALIDO);
            }

            getCliente(clienteId);
        }

        LocalDateTime fechaInicio =
                desde != null
                        ? desde.atStartOfDay()
                        : null;

        LocalDateTime fechaFin =
                hasta != null
                        ? hasta.plusDays(1).atStartOfDay()
                        : null;

        Pageable pageable = PageRequest.of(
                pagina,
                tamanio,
                Sort.by("fechaCreacion").descending()
        );

        Page<Cotizacion> cotizaciones =
                cotizacionRepository.buscar(
                        codigo,
                        clienteId,
                        estado,
                        fechaInicio,
                        fechaFin,
                        pageable
                );

        Page<CotizacionResponse> responses =
                cotizaciones.map(
                        cotizacionMapper::convertirCotizacionDto
                );

        return PaginaResponse.from(responses);
    }

    @Override
    @Transactional(readOnly = true)
    public CotizacionResponse findById(Long id) {
        validarId(id);

        Cotizacion cotizacion = getCotizacion(id);

        return cotizacionMapper.convertirCotizacionDto(
                cotizacion
        );
    }

    @Override
    @Transactional(readOnly = true)
    public CotizacionResponse findByCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException(
                    CODIGO_INVALIDO
            );
        }

        Cotizacion cotizacion =
                cotizacionRepository
                        .findByCodigo(codigo)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Cotización no encontrada."
                                )
                        );

        return cotizacionMapper.convertirCotizacionDto(
                cotizacion
        );
    }

    @Override
    @Transactional
    public CotizacionResponse create(CotizacionRequest request) {
        Empresa empresa = getEmpresa(
                request.getEmpresaId()
        );

        Cliente cliente = getCliente(
                request.getClienteId()
        );

        validarEmpresaActiva(empresa);
        validarClienteActivo(cliente);
        validarFechaVencimiento(
                request.getFechaVencimiento()
        );
        validarProductosDuplicados(
                request.getDetalles()
        );

        Cotizacion cotizacion = new Cotizacion();

        cotizacion.setCodigo(
                generarCodigoCotizacion()
        );
        cotizacion.setEmpresa(empresa);
        cotizacion.setCliente(cliente);
        cotizacion.setFechaVencimiento(
                request.getFechaVencimiento()
        );
        cotizacion.setObservacion(
                request.getObservacion()
        );
        cotizacion.setEstado(
                EstadoCotizacion.BORRADOR
        );
        cotizacion.setCreadoPor(
                SecurityUtils.getUsuarioActual()
        );

        List<DetalleCotizacion> detalles =
                crearDetalles(
                        cotizacion,
                        cliente,
                        request.getDetalles()
                );

        BigDecimal subtotal =
                calcularSubtotal(detalles);

        cotizacion.setDetalles(detalles);
        cotizacion.setSubtotal(subtotal);
        cotizacion.setTotal(subtotal);

        Cotizacion cotizacionGuardada =
                cotizacionRepository.save(
                        cotizacion
                );

        return cotizacionMapper.convertirCotizacionDto(
                cotizacionGuardada
        );
    }

    @Override
    @Transactional
    public CotizacionResponse update(Long id, CotizacionRequest request) {
        validarId(id);

        Cotizacion cotizacion =
                getCotizacion(id);

        validarCotizacionEditable(
                cotizacion
        );

        Empresa empresa = getEmpresa(
                request.getEmpresaId()
        );

        Cliente cliente = getCliente(
                request.getClienteId()
        );

        validarEmpresaActiva(empresa);
        validarClienteActivo(cliente);
        validarFechaVencimiento(
                request.getFechaVencimiento()
        );
        validarProductosDuplicados(
                request.getDetalles()
        );

        cotizacion.setEmpresa(empresa);
        cotizacion.setCliente(cliente);
        cotizacion.setFechaVencimiento(
                request.getFechaVencimiento()
        );
        cotizacion.setObservacion(
                request.getObservacion()
        );
        cotizacion.setActualizadoPor(
                SecurityUtils.getUsuarioActual()
        );

        cotizacion.getDetalles().clear();

        List<DetalleCotizacion> nuevosDetalles =
                crearDetalles(
                        cotizacion,
                        cliente,
                        request.getDetalles()
                );

        cotizacion.getDetalles().addAll(
                nuevosDetalles
        );

        BigDecimal subtotal =
                calcularSubtotal(
                        cotizacion.getDetalles()
                );

        cotizacion.setSubtotal(subtotal);
        cotizacion.setTotal(subtotal);

        Cotizacion cotizacionActualizada =
                cotizacionRepository.save(
                        cotizacion
                );

        return cotizacionMapper.convertirCotizacionDto(
                cotizacionActualizada
        );
    }

    @Override
    @Transactional
    public CotizacionResponse emitir(Long id) {
        validarId(id);

        Cotizacion cotizacion =
                getCotizacion(id);

        if (cotizacion.getEstado() !=
                EstadoCotizacion.BORRADOR) {

            throw new BusinessException(
                    "Solo una cotización en borrador puede ser emitida."
            );
        }

        validarCotizacionNoVencida(
                cotizacion
        );

        cotizacion.setEstado(
                EstadoCotizacion.EMITIDA
        );

        cotizacion.setActualizadoPor(
                SecurityUtils.getUsuarioActual()
        );

        Cotizacion cotizacionActualizada =
                cotizacionRepository.save(
                        cotizacion
                );

        return cotizacionMapper.convertirCotizacionDto(
                cotizacionActualizada
        );
    }

    @Override
    @Transactional
    public CotizacionResponse anular(Long id) {
        validarId(id);

        Cotizacion cotizacion =
                getCotizacion(id);

        if (cotizacion.getEstado() ==
                EstadoCotizacion.ANULADA) {

            throw new BusinessException(
                    "La cotización ya se encuentra anulada."
            );
        }

        cotizacion.setEstado(
                EstadoCotizacion.ANULADA
        );

        cotizacion.setActualizadoPor(
                SecurityUtils.getUsuarioActual()
        );

        Cotizacion cotizacionActualizada =
                cotizacionRepository.save(
                        cotizacion
                );

        return cotizacionMapper.convertirCotizacionDto(
                cotizacionActualizada
        );
    }

    // Metodos auxiliares
    private Cotizacion getCotizacion(Long id) {
        return cotizacionRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Cotización no encontrada."
                        )
                );
    }

    private Empresa getEmpresa(Long id) {
        return empresaRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                EMPRESA_NO_ENCONTRADA
                        )
                );
    }

    private Cliente getCliente(Long id) {
        return clienteRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                CLIENTE_NO_ENCONTRADO
                        )
                );
    }

    private Producto getProducto(Long id) {
        return productoRepository
                .findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                PRODUCTO_NO_ENCONTRADO
                        )
                );
    }

    private List<DetalleCotizacion> crearDetalles(Cotizacion cotizacion, Cliente cliente, List<DetalleCotizacionRequest> requests) {
        List<DetalleCotizacion> detalles =
                new ArrayList<>();

        for (DetalleCotizacionRequest request : requests) {
            Producto producto =
                    getProducto(
                            request.getProductoId()
                    );

            validarProductoActivo(producto);

            validarModalidadEnvase(
                    producto,
                    request
            );

            BigDecimal precioUnitario =
                    obtenerPrecioProducto(
                            cliente.getId(),
                            producto,
                            request
                    );

            BigDecimal subtotal =
                    precioUnitario.multiply(
                            BigDecimal.valueOf(
                                    request.getCantidad()
                            )
                    );

            DetalleCotizacion detalle =
                    new DetalleCotizacion();

            detalle.setCotizacion(cotizacion);
            detalle.setProducto(producto);
            detalle.setCantidad(
                    request.getCantidad()
            );
            detalle.setModalidadEnvase(
                    request.getModalidadEnvase()
            );
            detalle.setPrecioUnitario(
                    precioUnitario
            );
            detalle.setSubtotal(subtotal);

            detalles.add(detalle);
        }

        return detalles;
    }

    private BigDecimal obtenerPrecioProducto(Long clienteId, Producto producto, DetalleCotizacionRequest detalle) {
        if (producto.getCategoria() ==
                ProductoCategoria.BIDON &&
                detalle.getModalidadEnvase() ==
                        TipoMovimientoEnvase.COMPRA) {

            BigDecimal precioBase =
                    producto.getPrecioBase();

            if (detalle.getPrecioManual() == null) {
                return precioBase;
            }

            validarPrecioManualCompra(
                    detalle.getPrecioManual(),
                    precioBase
            );

            return detalle.getPrecioManual();
        }

        if (detalle.getPrecioManual() != null) {
            throw new BusinessException(
                    "El precio manual solo puede utilizarse en la compra de bidones."
            );
        }

        return clientePrecioRepository
                .findByClienteIdAndProductoId(
                        clienteId,
                        producto.getId()
                )
                .map(ClientePrecio::getPrecio)
                .orElse(
                        producto.getPrecioBase()
                );
    }

    private BigDecimal calcularSubtotal(List<DetalleCotizacion> detalles) {
        return detalles.stream()
                .map(
                        DetalleCotizacion::getSubtotal
                )
                .reduce(
                        BigDecimal.ZERO,
                        BigDecimal::add
                );
    }

    private void validarEmpresaActiva(Empresa empresa) {
        if (empresa.getEstado() !=
                EstadoGeneral.ACTIVO) {

            throw new BusinessException(
                    "La empresa seleccionada se encuentra inactiva."
            );
        }
    }

    private void validarClienteActivo(Cliente cliente) {
        if (cliente.getEstado() !=
                EstadoGeneral.ACTIVO) {

            throw new BusinessException(
                    CLIENTE_INACTIVO
            );
        }
    }

    private void validarProductoActivo(Producto producto) {
        if (producto.getEstado() !=
                EstadoGeneral.ACTIVO) {

            throw new BusinessException(
                    PRODUCTO_INACTIVO
            );
        }
    }

    private void validarProductosDuplicados(List<DetalleCotizacionRequest> detalles) {
        Set<Long> productosIds =
                new HashSet<>();

        for (DetalleCotizacionRequest detalle : detalles) {
            if (!productosIds.add(
                    detalle.getProductoId()
            )) {

                throw new BusinessException(
                        "La cotización no puede contener el mismo producto más de una vez."
                );
            }
        }
    }

    private void validarModalidadEnvase(Producto producto, DetalleCotizacionRequest detalle) {
        if (producto.getCategoria() ==
                ProductoCategoria.BIDON) {

            if (detalle.getModalidadEnvase() == null) {
                throw new BusinessException(
                        "La modalidad de envase es obligatoria para productos BIDON."
                );
            }

            if (detalle.getModalidadEnvase() !=
                    TipoMovimientoEnvase.INTERCAMBIO &&
                    detalle.getModalidadEnvase() !=
                            TipoMovimientoEnvase.PRESTAMO &&
                    detalle.getModalidadEnvase() !=
                            TipoMovimientoEnvase.COMPRA) {

                throw new BusinessException(
                        "La modalidad de envase no es válida para una cotización."
                );
            }

            return;
        }

        if (detalle.getModalidadEnvase() != null) {
            throw new BusinessException(
                    "La modalidad de envase solo puede utilizarse en productos BIDON."
            );
        }
    }

    private void validarPrecioManualCompra(BigDecimal precioManual, BigDecimal precioBase) {
        if (!SecurityUtils.esAdmin()) {
            throw new BusinessException(
                    "Solo un administrador puede modificar el precio de compra de un bidón."
            );
        }

        if (precioManual.compareTo(
                BigDecimal.ZERO
        ) <= 0) {

            throw new BusinessException(
                    "El precio de compra debe ser mayor a cero."
            );
        }

        if (precioManual.compareTo(
                precioBase
        ) > 0) {

            throw new BusinessException(
                    "El precio manual no puede superar el precio base del bidón."
            );
        }
    }

    private void validarFechaVencimiento(LocalDate fechaVencimiento) {
        if (fechaVencimiento != null &&
                fechaVencimiento.isBefore(
                        LocalDate.now()
                )) {

            throw new BusinessException(
                    "La fecha de vencimiento no puede ser anterior a la fecha actual."
            );
        }
    }

    private void validarCotizacionNoVencida(Cotizacion cotizacion) {
        if (cotizacion.getFechaVencimiento() != null &&
                cotizacion.getFechaVencimiento()
                        .isBefore(LocalDate.now())) {

            cotizacion.setEstado(
                    EstadoCotizacion.VENCIDA
            );

            cotizacionRepository.save(
                    cotizacion
            );

            throw new BusinessException(
                    "La cotización se encuentra vencida."
            );
        }
    }

    private void validarCotizacionEditable(Cotizacion cotizacion) {
        if (cotizacion.getEstado() !=
                EstadoCotizacion.BORRADOR) {

            throw new BusinessException(
                    "Solo una cotización en borrador puede modificarse."
            );
        }
    }

    private String generarCodigoCotizacion() {
        return "COT-" +
                UUID.randomUUID()
                        .toString()
                        .substring(0, 8)
                        .toUpperCase();
    }

    private void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    ID_INVALIDO
            );
        }
    }

    private void validarPaginacion(int pagina, int tamanio) {
        if (pagina < 0) {
            throw new IllegalArgumentException(
                    PAGINA_INVALIDA
            );
        }

        if (tamanio <= 0 ||
                tamanio > 100) {

            throw new IllegalArgumentException(
                    TAMANIO_PAGINA_INVALIDO
            );
        }
    }

    private void validarRangoFechas(LocalDate desde, LocalDate hasta) {
        if (desde != null &&
                hasta != null &&
                desde.isAfter(hasta)) {

            throw new IllegalArgumentException(
                    RANGO_FECHAS_INVALIDO
            );
        }
    }
}