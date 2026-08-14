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
import com.runicsoft.bencolapp.cotizaciones.repository.DetalleCotizacionRepository;
import com.runicsoft.bencolapp.cotizaciones.utils.EstadoCotizacion;
import com.runicsoft.bencolapp.cotizaciones.utils.TipoPrecioCotizacion;
import com.runicsoft.bencolapp.empresa.models.Empresa;
import com.runicsoft.bencolapp.empresa.repository.EmpresaRepository;
import com.runicsoft.bencolapp.productos.models.Producto;
import com.runicsoft.bencolapp.productos.repository.ProductoRepository;
import com.runicsoft.bencolapp.seguridad.utils.SecurityUtils;
import com.runicsoft.bencolapp.utils.EstadoGeneral;
import com.runicsoft.bencolapp.utils.exceptions.BusinessException;
import com.runicsoft.bencolapp.utils.exceptions.ResourceNotFoundException;
import com.runicsoft.bencolapp.utils.pagination.PaginaResponse;
import com.runicsoft.bencolapp.utils.storage.StorageProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static com.runicsoft.bencolapp.utils.constants.MessageConstants.*;

@Service
@RequiredArgsConstructor
public class CotizacionServiceImpl implements CotizacionService {

    private final CotizacionRepository cotizacionRepository;
    private final EmpresaRepository empresaRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final CotizacionMapper cotizacionMapper;
    private final DetalleCotizacionRepository detalleCotizacionRepository;
    private final StorageProperties storageProperties;

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

        LocalDateTime fechaInicio = desde != null ? desde.atStartOfDay() : null;
        LocalDateTime fechaFin = hasta != null ? hasta.plusDays(1).atStartOfDay() : null;

        Pageable pageable = PageRequest.of(
                pagina,
                tamanio,
                Sort.by("fechaCreacion").descending()
        );

        Page<Cotizacion> cotizaciones = cotizacionRepository.buscar(
                codigo,
                clienteId,
                estado,
                fechaInicio,
                fechaFin,
                pageable
        );

        Page<CotizacionResponse> responses = cotizaciones.map(cotizacionMapper::convertirCotizacionDto);
        return PaginaResponse.from(responses);
    }

    @Override
    @Transactional(readOnly = true)
    public CotizacionResponse findById(Long id) {
        validarId(id);

        Cotizacion cotizacion = getCotizacion(id);
        return cotizacionMapper.convertirCotizacionDto(cotizacion);
    }

    @Override
    @Transactional(readOnly = true)
    public CotizacionResponse findByCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException(CODIGO_INVALIDO);
        }

        Cotizacion cotizacion = cotizacionRepository.findByCodigo(codigo)
                .orElseThrow(() -> new ResourceNotFoundException("La cotización no fue encontrada."));

        return cotizacionMapper.convertirCotizacionDto(cotizacion);
    }

    @Override
    @Transactional
    public CotizacionResponse create(CotizacionRequest request) {
        Empresa empresa = getEmpresaActiva();
        Cliente cliente = getCliente(request.getClienteId());

        validarClienteActivo(cliente);
        validarFechaVencimiento(request.getFechaVencimiento());
        validarProductosDuplicados(request.getDetalles());

        Cotizacion cotizacion = new Cotizacion();
        cotizacion.setCodigo(generarCodigoCotizacion());
        cotizacion.setEmpresa(empresa);
        cotizacion.setCliente(cliente);
        cotizacion.setPorcentajeImpuesto(request.getPorcentajeImpuesto());
        cotizacion.setTipoPrecio(request.getTipoPrecio());
        cotizacion.setFechaVencimiento(request.getFechaVencimiento());
        cotizacion.setCondicionesPago(request.getCondicionesPago());
        cotizacion.setPlazoEntrega(request.getPlazoEntrega());
        cotizacion.setObservacion(request.getObservacion());
        cotizacion.setCreadoPor(SecurityUtils.getUsuarioActual());

        cargarDetalles(cotizacion, request.getDetalles());

        calcularTotales(cotizacion);

        Cotizacion cotizacionGuardada = cotizacionRepository.save(cotizacion);
        return cotizacionMapper.convertirCotizacionDto(cotizacionGuardada);
    }

    @Override
    @Transactional
    public CotizacionResponse update(Long id, CotizacionRequest request) {
        validarId(id);

        Cotizacion cotizacion = getCotizacion(id);

        if (cotizacion.getEstado() != EstadoCotizacion.BORRADOR) {
            throw new BusinessException("Solo una cotización en borrador puede ser modificada.");
        }

        Empresa empresa = getEmpresaActiva();
        Cliente cliente = getCliente(request.getClienteId());

        validarClienteActivo(cliente);
        validarFechaVencimiento(request.getFechaVencimiento());
        validarProductosDuplicados(request.getDetalles());

        cotizacion.setEmpresa(empresa);
        cotizacion.setCliente(cliente);
        cotizacion.setPorcentajeImpuesto(request.getPorcentajeImpuesto());
        cotizacion.setTipoPrecio(request.getTipoPrecio());
        cotizacion.setFechaVencimiento(request.getFechaVencimiento());
        cotizacion.setCondicionesPago(request.getCondicionesPago());
        cotizacion.setPlazoEntrega(request.getPlazoEntrega());
        cotizacion.setObservacion(request.getObservacion());
        cotizacion.setActualizadoPor(SecurityUtils.getUsuarioActual());

        actualizarDetalles(cotizacion, request.getDetalles());

        calcularTotales(cotizacion);

        Cotizacion cotizacionActualizada = cotizacionRepository.save(cotizacion);
        return cotizacionMapper.convertirCotizacionDto(cotizacionActualizada);
    }

    @Override
    @Transactional
    public CotizacionResponse emitir(Long id) {
        validarId(id);

        Cotizacion cotizacion = getCotizacion(id);

        if (cotizacion.getEstado() != EstadoCotizacion.BORRADOR) {
            throw new BusinessException("Solo una cotización en borrador puede ser emitida.");
        }

        if (cotizacion.getFechaVencimiento() != null &&
                cotizacion.getFechaVencimiento().isBefore(LocalDate.now())) {
            throw new BusinessException("No se puede emitir una cotización vencida.");
        }

        cotizacion.setEstado(EstadoCotizacion.EMITIDA);
        cotizacion.setActualizadoPor(SecurityUtils.getUsuarioActual());

        Cotizacion cotizacionActualizada = cotizacionRepository.save(cotizacion);
        return cotizacionMapper.convertirCotizacionDto(cotizacionActualizada);
    }

    @Override
    @Transactional
    public CotizacionResponse anular(Long id) {
        validarId(id);

        Cotizacion cotizacion = getCotizacion(id);

        if (cotizacion.getEstado() == EstadoCotizacion.ANULADA) {
            throw new BusinessException("La cotización ya se encuentra anulada.");
        }

        cotizacion.setEstado(EstadoCotizacion.ANULADA);
        cotizacion.setActualizadoPor(SecurityUtils.getUsuarioActual());

        Cotizacion cotizacionActualizada = cotizacionRepository.save(cotizacion);
        return cotizacionMapper.convertirCotizacionDto(cotizacionActualizada);
    }

    @Override
    @Transactional
    public CotizacionResponse subirImagenDetalle(Long cotizacionId, Long detalleId, MultipartFile archivo) {
        validarId(cotizacionId);
        validarId(detalleId);

        Cotizacion cotizacion = getCotizacion(cotizacionId);

        DetalleCotizacion detalle = detalleCotizacionRepository
                .findByIdAndCotizacionId(detalleId, cotizacionId)
                .orElseThrow(() -> new ResourceNotFoundException("El detalle de la cotización no fue encontrado."));

        validarImagenDetalle(archivo);

        try {
            Path directorio = Paths.get(
                    storageProperties.getRoot(),
                    "cotizaciones",
                    cotizacion.getCodigo()
            ).toAbsolutePath().normalize();

            Files.createDirectories(directorio);

            eliminarImagenAnterior(detalle);

            String extension = obtenerExtension(archivo.getOriginalFilename());

            String nombreArchivo =
                    "detalle-" +
                            detalle.getId() +
                            "-" +
                            UUID.randomUUID() +
                            extension;

            Path rutaArchivo = directorio
                    .resolve(nombreArchivo)
                    .normalize();

            Files.copy(
                    archivo.getInputStream(),
                    rutaArchivo,
                    StandardCopyOption.REPLACE_EXISTING
            );

            detalle.setImagenNombre(archivo.getOriginalFilename());
            detalle.setImagenTipo(archivo.getContentType());
            detalle.setImagenRuta(rutaArchivo.toString());

            detalleCotizacionRepository.save(detalle);

            return cotizacionMapper.convertirCotizacionDto(cotizacion);

        } catch (IOException e) {
            throw new BusinessException("No fue posible guardar la imagen del producto cotizado.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Resource obtenerImagenDetalle(Long cotizacionId, Long detalleId) {
        validarId(cotizacionId);
        validarId(detalleId);

        DetalleCotizacion detalle = detalleCotizacionRepository
                .findByIdAndCotizacionId(detalleId, cotizacionId)
                .orElseThrow(() -> new ResourceNotFoundException("El detalle de la cotización no fue encontrado."));

        if (detalle.getImagenRuta() == null || detalle.getImagenRuta().isBlank()) {
            throw new ResourceNotFoundException("El detalle no tiene una imagen registrada.");
        }

        try {
            Path ruta = Paths.get(detalle.getImagenRuta())
                    .toAbsolutePath()
                    .normalize();

            Resource recurso = new UrlResource(ruta.toUri());

            if (!recurso.exists() || !recurso.isReadable()) {
                throw new ResourceNotFoundException("La imagen del producto no fue encontrada.");
            }

            return recurso;

        } catch (MalformedURLException e) {
            throw new ResourceNotFoundException("La imagen del producto no fue encontrada.");
        }
    }

    @Override
    @Transactional
    public CotizacionResponse eliminarImagenDetalle(Long cotizacionId, Long detalleId) {
        validarId(cotizacionId);
        validarId(detalleId);

        Cotizacion cotizacion = getCotizacion(cotizacionId);

        DetalleCotizacion detalle = detalleCotizacionRepository
                .findByIdAndCotizacionId(detalleId, cotizacionId)
                .orElseThrow(() -> new ResourceNotFoundException("El detalle de la cotización no fue encontrado."));

        if (detalle.getImagenRuta() == null || detalle.getImagenRuta().isBlank()) {
            throw new BusinessException("El producto cotizado no tiene una imagen registrada.");
        }

        eliminarImagenAnterior(detalle);

        detalle.setImagenNombre(null);
        detalle.setImagenTipo(null);
        detalle.setImagenRuta(null);

        detalleCotizacionRepository.save(detalle);

        return cotizacionMapper.convertirCotizacionDto(cotizacion);
    }

    private void cargarDetalles(Cotizacion cotizacion, List<DetalleCotizacionRequest> requests) {
        List<DetalleCotizacion> detalles = new ArrayList<>();

        for (DetalleCotizacionRequest request : requests) {
            Producto producto = getProducto(request.getProductoId());
            validarProductoActivo(producto);

            DetalleCotizacion detalle = crearDetalle(
                    cotizacion,
                    producto,
                    request
            );

            detalles.add(detalle);
        }

        cotizacion.setDetalles(detalles);
    }

    private void actualizarDetalles(Cotizacion cotizacion, List<DetalleCotizacionRequest> requests) {
        Map<Long, DetalleCotizacion> existentes = new HashMap<>();

        for (DetalleCotizacion detalle : cotizacion.getDetalles()) {
            existentes.put(
                    detalle.getProducto().getId(),
                    detalle
            );
        }

        List<DetalleCotizacion> nuevosDetalles = new ArrayList<>();

        for (DetalleCotizacionRequest request : requests) {
            Producto producto = getProducto(request.getProductoId());
            validarProductoActivo(producto);

            DetalleCotizacion detalleExistente = existentes.get(producto.getId());

            DetalleCotizacion detalle;

            if (detalleExistente != null) {
                detalle = detalleExistente;
                detalle.setCantidad(request.getCantidad());
                detalle.setPrecioUnitario(request.getPrecioUnitario());
                detalle.setSubtotal(
                        calcularSubtotal(
                                request.getCantidad(),
                                request.getPrecioUnitario()
                        )
                );
                detalle.setFrecuenciaAbastecimiento(request.getFrecuenciaAbastecimiento());
                detalle.setDescripcionAdicional(request.getDescripcionAdicional());
            } else {
                detalle = crearDetalle(
                        cotizacion,
                        producto,
                        request
                );
            }

            nuevosDetalles.add(detalle);
        }

        cotizacion.getDetalles().clear();
        cotizacion.getDetalles().addAll(nuevosDetalles);
    }

    private DetalleCotizacion crearDetalle(Cotizacion cotizacion, Producto producto, DetalleCotizacionRequest request) {
        DetalleCotizacion detalle = new DetalleCotizacion();
        detalle.setCotizacion(cotizacion);
        detalle.setProducto(producto);
        detalle.setCantidad(request.getCantidad());
        detalle.setPrecioUnitario(request.getPrecioUnitario());
        detalle.setSubtotal(
                calcularSubtotal(
                        request.getCantidad(),
                        request.getPrecioUnitario()
                )
        );
        detalle.setFrecuenciaAbastecimiento(request.getFrecuenciaAbastecimiento());
        detalle.setDescripcionAdicional(request.getDescripcionAdicional());

        return detalle;
    }

    private BigDecimal calcularSubtotal(Integer cantidad, BigDecimal precioUnitario) {
        return precioUnitario
                .multiply(BigDecimal.valueOf(cantidad))
                .setScale(2, RoundingMode.HALF_UP);
    }

    private void calcularTotales(Cotizacion cotizacion) {
        BigDecimal importeProductos = cotizacion.getDetalles()
                .stream()
                .map(DetalleCotizacion::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal porcentaje = cotizacion.getPorcentajeImpuesto()
                .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);

        BigDecimal subtotal;
        BigDecimal montoImpuesto;
        BigDecimal total;

        if (cotizacion.getTipoPrecio() == TipoPrecioCotizacion.CON_IGV) {
            BigDecimal divisor = BigDecimal.ONE.add(porcentaje);

            total = importeProductos;

            subtotal = total
                    .divide(divisor, 2, RoundingMode.HALF_UP);

            montoImpuesto = total
                    .subtract(subtotal)
                    .setScale(2, RoundingMode.HALF_UP);
        } else {
            subtotal = importeProductos;

            montoImpuesto = subtotal
                    .multiply(porcentaje)
                    .setScale(2, RoundingMode.HALF_UP);

            total = subtotal
                    .add(montoImpuesto)
                    .setScale(2, RoundingMode.HALF_UP);
        }

        cotizacion.setSubtotal(subtotal);
        cotizacion.setMontoImpuesto(montoImpuesto);
        cotizacion.setTotal(total);
    }

    private Cotizacion getCotizacion(Long id) {
        return cotizacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("La cotización no fue encontrada."));
    }

    private Cliente getCliente(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(CLIENTE_NO_ENCONTRADO));
    }

    private Producto getProducto(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(PRODUCTO_NO_ENCONTRADO));
    }

    private void validarClienteActivo(Cliente cliente) {
        if (cliente.getEstado() != EstadoGeneral.ACTIVO) {
            throw new BusinessException(CLIENTE_INACTIVO);
        }
    }

    private void validarProductoActivo(Producto producto) {
        if (producto.getEstado() != EstadoGeneral.ACTIVO) {
            throw new BusinessException(PRODUCTO_INACTIVO);
        }
    }

    private void validarProductosDuplicados(List<DetalleCotizacionRequest> detalles) {
        Set<Long> productos = new HashSet<>();

        for (DetalleCotizacionRequest detalle : detalles) {
            if (!productos.add(detalle.getProductoId())) {
                throw new BusinessException(
                        "No se puede agregar el mismo producto más de una vez en la cotización."
                );
            }
        }
    }

    private void validarFechaVencimiento(LocalDate fechaVencimiento) {
        if (fechaVencimiento != null &&
                fechaVencimiento.isBefore(LocalDate.now())) {
            throw new BusinessException(
                    "La fecha de vencimiento no puede ser anterior a la fecha actual."
            );
        }
    }

    private String generarCodigoCotizacion() {
        return "COT-" + UUID.randomUUID()
                .toString()
                .substring(0, 8)
                .toUpperCase();
    }

    private void validarId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(ID_INVALIDO);
        }
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

    private Empresa getEmpresaActiva() {
        return empresaRepository
                .findFirstByEstadoOrderByIdAsc(EstadoGeneral.ACTIVO)
                .orElseThrow(() -> new ResourceNotFoundException(EMPRESA_NO_ENCONTRADA));
    }

    private void validarImagenDetalle(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) {
            throw new BusinessException("La imagen del producto es obligatoria.");
        }

        String tipo = archivo.getContentType();

        if (tipo == null ||
                (!tipo.equals("image/png") &&
                        !tipo.equals("image/jpeg") &&
                        !tipo.equals("image/webp"))) {
            throw new BusinessException("La imagen debe ser PNG, JPG, JPEG o WEBP.");
        }

        long tamanioMaximo = 5L * 1024L * 1024L;

        if (archivo.getSize() > tamanioMaximo) {
            throw new BusinessException("La imagen no debe superar los 5 MB.");
        }
    }

    private String obtenerExtension(String nombreArchivo) {
        if (nombreArchivo == null || nombreArchivo.isBlank()) {
            return "";
        }

        int indice = nombreArchivo.lastIndexOf('.');

        if (indice == -1) {
            return "";
        }

        return nombreArchivo.substring(indice);
    }

    private void eliminarImagenAnterior(DetalleCotizacion detalle) {
        if (detalle.getImagenRuta() == null || detalle.getImagenRuta().isBlank()) {
            return;
        }

        try {
            Files.deleteIfExists(
                    Paths.get(detalle.getImagenRuta())
                            .toAbsolutePath()
                            .normalize()
            );
        } catch (IOException ignored) {
        }
    }
}