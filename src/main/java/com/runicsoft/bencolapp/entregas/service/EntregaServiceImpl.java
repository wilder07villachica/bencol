package com.runicsoft.bencolapp.entregas.service;

import com.runicsoft.bencolapp.entregas.dtos.request.ActualizarEstadoEntregaRequest;
import com.runicsoft.bencolapp.entregas.dtos.request.AsignarEntregaRequest;
import com.runicsoft.bencolapp.entregas.dtos.request.ObservacionEntregaRequest;
import com.runicsoft.bencolapp.entregas.dtos.response.EntregaResponse;
import com.runicsoft.bencolapp.entregas.dtos.response.HistorialEntregaResponse;
import com.runicsoft.bencolapp.entregas.dtos.response.ResponsableEntregaResponse;
import com.runicsoft.bencolapp.entregas.mapper.EntregaMapper;
import com.runicsoft.bencolapp.entregas.models.Entrega;
import com.runicsoft.bencolapp.entregas.models.HistorialEntrega;
import com.runicsoft.bencolapp.entregas.repository.EntregaRepository;
import com.runicsoft.bencolapp.entregas.repository.HistorialEntregaRepository;
import com.runicsoft.bencolapp.entregas.utils.EstadoEntrega;
import com.runicsoft.bencolapp.seguridad.models.Usuario;
import com.runicsoft.bencolapp.seguridad.repository.UsuarioRepository;
import com.runicsoft.bencolapp.seguridad.utils.RolUsuario;
import com.runicsoft.bencolapp.seguridad.utils.SecurityUtils;
import com.runicsoft.bencolapp.utils.EstadoGeneral;
import com.runicsoft.bencolapp.utils.exceptions.BusinessException;
import com.runicsoft.bencolapp.utils.exceptions.ConflictException;
import com.runicsoft.bencolapp.utils.exceptions.ResourceNotFoundException;
import com.runicsoft.bencolapp.utils.pagination.PaginaResponse;
import com.runicsoft.bencolapp.ventas.models.Venta;
import com.runicsoft.bencolapp.ventas.repository.VentaRepository;
import com.runicsoft.bencolapp.ventas.utils.EstadoVenta;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EntregaServiceImpl implements EntregaService {

    private static final List<RolUsuario> ROLES_OPERATIVOS = List.of(
            RolUsuario.ADMIN,
            RolUsuario.VENTAS,
            RolUsuario.REPARTIDOR
    );

    private final EntregaRepository entregaRepository;
    private final HistorialEntregaRepository historialEntregaRepository;
    private final UsuarioRepository usuarioRepository;
    private final VentaRepository ventaRepository;
    private final EntregaMapper entregaMapper;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void crearParaVenta(Venta venta) {
        Entrega entrega = new Entrega();

        entrega.setVenta(venta);
        entrega.setDireccion(venta.getCliente().getDireccion());
        entrega.setEstado(EstadoEntrega.PENDIENTE);
        entrega.setFechaCreacion(ahora());
        entrega.setFechaActualizacion(entrega.getFechaCreacion());

        entregaRepository.save(entrega);

        registrarHistorial(
                entrega,
                "CREACION",
                null,
                "Entrega generada al registrar la venta."
        );
    }

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void alAnularVenta(Venta venta) {
        entregaRepository.findByVentaId(venta.getId())
                .ifPresent(entrega -> {
                    EstadoEntrega estadoAnterior = entrega.getEstado();

                    if (estadoAnterior == EstadoEntrega.PENDIENTE
                            || estadoAnterior == EstadoEntrega.REPROGRAMADO) {
                        entrega.setEstado(EstadoEntrega.CANCELADO);
                    }

                    entrega.setFechaActualizacion(ahora());

                    registrarHistorial(
                            entrega,
                            "VENTA_ANULADA",
                            estadoAnterior,
                            "Venta anulada en el sistema administrativo."
                    );
                });
    }

    @Override
    public PaginaResponse<EntregaResponse> listar(int pagina, int tamanio, EstadoEntrega estado) {
        Usuario usuario = obtenerUsuarioActual();

        Specification<Entrega> filtro = (root, query, cb) -> cb.conjunction();

        if (usuario.getRol() == RolUsuario.REPARTIDOR) {
            filtro = filtro.and((root, query, cb) ->
                    cb.equal(
                            root.get("repartidor").get("id"),
                            usuario.getId()
                    )
            );
        }

        if (estado != null) {
            filtro = filtro.and((root, query, cb) ->
                    cb.equal(root.get("estado"), estado)
            );
        }

        return PaginaResponse.from(
                entregaRepository.findAll(
                        filtro,
                        obtenerPaginacion(pagina, tamanio)
                ).map(entregaMapper::convertirEntregaDto)
        );
    }

    @Override
    public EntregaResponse obtener(Long id) {
        Usuario usuario = obtenerUsuarioActual();
        Entrega entrega = getEntrega(id);

        validarAcceso(entrega, usuario);

        return entregaMapper.convertirEntregaDto(entrega);
    }

    @Override
    public PaginaResponse<ResponsableEntregaResponse> responsables(int pagina, int tamanio) {
        Usuario usuario = obtenerUsuarioActual();

        if (usuario.getRol() == RolUsuario.REPARTIDOR) {
            throw new AccessDeniedException(
                    "No puede consultar responsables."
            );
        }

        return PaginaResponse.from(
                usuarioRepository.findByEstadoAndRolIn(
                        EstadoGeneral.ACTIVO,
                        ROLES_OPERATIVOS,
                        obtenerPaginacion(pagina, tamanio)
                ).map(entregaMapper::convertirResponsableDto)
        );
    }

    @Override
    @Transactional
    public EntregaResponse asignar(Long id, AsignarEntregaRequest request) {
        Usuario usuario = obtenerUsuarioActual();

        if (usuario.getRol() == RolUsuario.REPARTIDOR) {
            throw new AccessDeniedException(
                    "No puede asignar entregas."
            );
        }

        Entrega entrega = obtenerParaActualizar(
                id,
                usuario,
                request.getVersion()
        );

        validarEntregaAbierta(entrega);

        Usuario responsable = usuarioRepository.findById(
                request.getRepartidorId()
        ).orElseThrow(() ->
                new ResourceNotFoundException(
                        "Responsable no encontrado."
                )
        );

        if (responsable.getEstado() != EstadoGeneral.ACTIVO
                || responsable.getRol() == null
                || !ROLES_OPERATIVOS.contains(responsable.getRol())) {
            throw new BusinessException(
                    "El responsable debe estar activo y tener un rol operativo."
            );
        }

        Long responsableAnteriorId =
                entrega.getRepartidor() == null
                        ? null
                        : entrega.getRepartidor().getId();

        if (Objects.equals(
                responsableAnteriorId,
                responsable.getId()
        )) {
            return entregaMapper.convertirEntregaDto(entrega);
        }

        entrega.setRepartidor(responsable);

        registrarHistorial(
                entrega,
                "ASIGNACION",
                entrega.getEstado(),
                "Responsable anterior: " + responsableAnteriorId
                        + ". Nuevo responsable: "
                        + responsable.getId()
                        + " (" + responsable.getUsername() + ")."
        );

        return guardarEntrega(entrega);
    }

    @Override
    @Transactional
    public EntregaResponse actualizarEstado(Long id, ActualizarEstadoEntregaRequest request) {
        Entrega entrega = obtenerParaActualizar(
                id,
                obtenerUsuarioActual(),
                request.getVersion()
        );

        validarEntregaAbierta(entrega);

        if (request.getEstado() == null
                || request.getEstado() == EstadoEntrega.PENDIENTE) {
            throw new BusinessException(
                    "Seleccione entregado, reprogramado o cancelado."
            );
        }

        String observaciones = limpiar(request.getObservaciones());

        if ((request.getEstado() == EstadoEntrega.REPROGRAMADO
                || request.getEstado() == EstadoEntrega.CANCELADO)
                && observaciones == null) {
            throw new BusinessException(
                    "Debe indicar el motivo."
            );
        }

        if (request.getEstado() == EstadoEntrega.REPROGRAMADO) {
            if (request.getFechaProgramada() == null
                    || !request.getFechaProgramada().isAfter(ahora())) {
                throw new BusinessException(
                        "La reprogramación requiere una fecha futura de Lima."
                );
            }
        } else if (request.getFechaProgramada() != null) {
            throw new BusinessException(
                    "La fecha programada solo se recibe al reprogramar."
            );
        }

        if (request.getEstado() == EstadoEntrega.ENTREGADO
                && entrega.getRepartidor() == null) {
            throw new BusinessException(
                    "Asigne un responsable antes de confirmar la entrega."
            );
        }

        EstadoEntrega estadoAnterior = entrega.getEstado();

        entrega.setEstado(request.getEstado());

        if (observaciones != null) {
            entrega.setObservaciones(observaciones);
        }

        if (request.getEstado() == EstadoEntrega.REPROGRAMADO) {
            entrega.setFechaProgramada(request.getFechaProgramada());
        }

        if (request.getEstado() == EstadoEntrega.ENTREGADO) {
            entrega.setFechaEntrega(ahora());
        }

        registrarHistorial(
                entrega,
                "ESTADO",
                estadoAnterior,
                observaciones
        );

        if (request.getEstado() == EstadoEntrega.REPROGRAMADO) {
            registrarHistorial(
                    entrega,
                    "PROGRAMACION",
                    entrega.getEstado(),
                    "Nueva fecha de entrega: "
                            + request.getFechaProgramada()
                            + " (America/Lima)."
            );
        }

        return guardarEntrega(entrega);
    }

    @Override
    @Transactional
    public EntregaResponse registrarObservacion(Long id, ObservacionEntregaRequest request) {
        Entrega entrega = obtenerParaActualizar(
                id,
                obtenerUsuarioActual(),
                request.getVersion()
        );

        String observaciones = limpiar(request.getObservaciones());

        if (observaciones == null) {
            throw new BusinessException(
                    "La observación es obligatoria."
            );
        }

        entrega.setObservaciones(observaciones);

        registrarHistorial(
                entrega,
                "OBSERVACION",
                entrega.getEstado(),
                observaciones
        );

        return guardarEntrega(entrega);
    }

    @Override
    public PaginaResponse<HistorialEntregaResponse> historial(Long id, int pagina, int tamanio) {
        Usuario usuario = obtenerUsuarioActual();
        Entrega entrega = getEntrega(id);

        validarAcceso(entrega, usuario);

        return PaginaResponse.from(
                historialEntregaRepository.findByEntregaId(
                        id,
                        obtenerPaginacion(pagina, tamanio)
                ).map(entregaMapper::convertirHistorialDto)
        );
    }

    private Entrega obtenerParaActualizar(Long id, Usuario usuario, Long version) {
        Entrega entrega = getEntrega(id);

        validarAcceso(entrega, usuario);

        Venta venta = ventaRepository.findByIdForUpdate(
                entrega.getVenta().getId()
        ).orElseThrow(() ->
                new ResourceNotFoundException(
                        "Venta no encontrada."
                )
        );

        if (version == null
                || !Objects.equals(entrega.getVersion(), version)) {
            throw new ConflictException(
                    "La entrega cambió. Actualice antes de volver a guardar."
            );
        }

        if (venta.getEstado() == EstadoVenta.ANULADA) {
            throw new BusinessException(
                    "La venta está anulada."
            );
        }

        return entrega;
    }

    private Usuario obtenerUsuarioActual() {
        Usuario usuario = usuarioRepository.findByUsername(
                SecurityUtils.getUsuarioActual()
        ).orElseThrow(() ->
                new AccessDeniedException(
                        "Usuario no autorizado."
                )
        );

        if (usuario.getEstado() != EstadoGeneral.ACTIVO
                || usuario.getRol() == null
                || !ROLES_OPERATIVOS.contains(usuario.getRol())) {
            throw new AccessDeniedException(
                    "Usuario no autorizado para entregas."
            );
        }

        return usuario;
    }

    private void validarAcceso(Entrega entrega, Usuario usuario) {
        Long responsableId =
                entrega.getRepartidor() == null
                        ? null
                        : entrega.getRepartidor().getId();

        if (usuario.getRol() == RolUsuario.REPARTIDOR
                && !Objects.equals(
                responsableId,
                usuario.getId()
        )) {
            throw new AccessDeniedException(
                    "La entrega no está asignada a este usuario."
            );
        }
    }

    private Entrega getEntrega(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "El identificador no es válido."
            );
        }

        return entregaRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Entrega no encontrada."
                        )
                );
    }

    private void validarEntregaAbierta(Entrega entrega) {
        if (entrega.getEstado() == EstadoEntrega.ENTREGADO
                || entrega.getEstado() == EstadoEntrega.CANCELADO) {
            throw new BusinessException(
                    "La entrega ya está finalizada."
            );
        }
    }

    private EntregaResponse guardarEntrega(Entrega entrega) {
        entrega.setFechaActualizacion(ahora());

        Entrega entregaGuardada =
                entregaRepository.saveAndFlush(entrega);

        return entregaMapper.convertirEntregaDto(entregaGuardada);
    }

    private void registrarHistorial(Entrega entrega, String accion, EstadoEntrega estadoAnterior, String observaciones) {
        HistorialEntrega historial = new HistorialEntrega();

        historial.setEntrega(entrega);
        historial.setAccion(accion);

        historial.setEstadoAnterior(
                estadoAnterior == null
                        ? null
                        : estadoAnterior.name()
        );

        historial.setEstadoNuevo(entrega.getEstado().name());
        historial.setObservaciones(observaciones);
        historial.setUsuario(SecurityUtils.getUsuarioActual());
        historial.setFecha(ahora());

        historialEntregaRepository.save(historial);
    }

    private Pageable obtenerPaginacion(int pagina, int tamanio) {
        if (pagina < 0 || tamanio < 1 || tamanio > 100) {
            throw new IllegalArgumentException(
                    "La paginación no es válida."
            );
        }

        return PageRequest.of(
                pagina,
                tamanio,
                Sort.by("id").descending()
        );
    }

    private LocalDateTime ahora() {
        return LocalDateTime.now(
                ZoneId.of("America/Lima")
        );
    }

    private String limpiar(String texto) {
        return texto == null || texto.isBlank()
                ? null
                : texto.trim();
    }
}