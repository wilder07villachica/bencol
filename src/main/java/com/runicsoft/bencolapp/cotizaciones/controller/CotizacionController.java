package com.runicsoft.bencolapp.cotizaciones.controller;

import com.runicsoft.bencolapp.cotizaciones.dtos.request.CotizacionRequest;
import com.runicsoft.bencolapp.cotizaciones.dtos.response.CotizacionResponse;
import com.runicsoft.bencolapp.cotizaciones.service.CotizacionPdfService;
import com.runicsoft.bencolapp.cotizaciones.service.CotizacionService;
import com.runicsoft.bencolapp.cotizaciones.utils.EstadoCotizacion;
import com.runicsoft.bencolapp.utils.exceptions.ResourceNotFoundException;
import com.runicsoft.bencolapp.utils.pagination.PaginaResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@RestController
@RequestMapping("/bencol.agua/cotizaciones")
@RequiredArgsConstructor
public class CotizacionController {

    private final CotizacionService cotizacionService;
    private final CotizacionPdfService cotizacionPdfService;

    @GetMapping
    public ResponseEntity<PaginaResponse<CotizacionResponse>> findAll(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanio,
            @RequestParam(required = false) String codigo,
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) EstadoCotizacion estado,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate desde,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate hasta
    ) {
        return ResponseEntity.ok(
                cotizacionService.findAll(
                        pagina,
                        tamanio,
                        codigo,
                        clienteId,
                        estado,
                        desde,
                        hasta
                )
        );
    }

    @GetMapping("/{idCotizacion}")
    public ResponseEntity<CotizacionResponse> findById(@PathVariable Long idCotizacion) {
        return ResponseEntity.ok(
                cotizacionService.findById(idCotizacion)
        );
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<CotizacionResponse> findByCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(
                cotizacionService.findByCodigo(codigo)
        );
    }

    @PostMapping
    public ResponseEntity<CotizacionResponse> create(@Valid @RequestBody CotizacionRequest request) {
        CotizacionResponse response = cotizacionService.create(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @PutMapping("/{idCotizacion}")
    public ResponseEntity<CotizacionResponse> update(@PathVariable Long idCotizacion, @Valid @RequestBody CotizacionRequest request) {
        CotizacionResponse response = cotizacionService.update(
                idCotizacion,
                request
        );

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{idCotizacion}/emitir")
    public ResponseEntity<CotizacionResponse> emitir(@PathVariable Long idCotizacion) {
        return ResponseEntity.ok(
                cotizacionService.emitir(idCotizacion)
        );
    }

    @PatchMapping("/{idCotizacion}/anular")
    public ResponseEntity<CotizacionResponse> anular(@PathVariable Long idCotizacion) {
        return ResponseEntity.ok(
                cotizacionService.anular(idCotizacion)
        );
    }

    @PostMapping(value = "/{cotizacionId}/detalles/{detalleId}/imagen", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CotizacionResponse> subirImagenDetalle(@PathVariable Long cotizacionId, @PathVariable Long detalleId, @RequestPart("archivo") MultipartFile archivo) {
        return ResponseEntity.ok(
                cotizacionService.subirImagenDetalle(
                        cotizacionId,
                        detalleId,
                        archivo
                )
        );
    }

    @GetMapping("/{cotizacionId}/detalles/{detalleId}/imagen")
    public ResponseEntity<Resource> obtenerImagenDetalle(@PathVariable Long cotizacionId, @PathVariable Long detalleId) {
        CotizacionResponse cotizacion = cotizacionService.findById(cotizacionId);

        var detalle = cotizacion.getDetalles()
                .stream()
                .filter(item -> item.getId().equals(detalleId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("El detalle de la cotización no fue encontrado."));

        Resource recurso = cotizacionService.obtenerImagenDetalle(
                cotizacionId,
                detalleId
        );

        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;

        if (detalle.getImagenTipo() != null) {
            mediaType = MediaType.parseMediaType(
                    detalle.getImagenTipo()
            );
        }

        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(recurso);
    }

    @DeleteMapping("/{cotizacionId}/detalles/{detalleId}/imagen")
    public ResponseEntity<CotizacionResponse> eliminarImagenDetalle(@PathVariable Long cotizacionId, @PathVariable Long detalleId) {
        return ResponseEntity.ok(
                cotizacionService.eliminarImagenDetalle(
                        cotizacionId,
                        detalleId
                )
        );
    }

    @GetMapping("/{idCotizacion}/pdf")
    public ResponseEntity<byte[]> generarPdf(@PathVariable Long idCotizacion) {
        CotizacionResponse cotizacion = cotizacionService.findById(idCotizacion);

        byte[] pdf = cotizacionPdfService.generarPdf(idCotizacion);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + cotizacion.getCodigo() + ".pdf\""
                )
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}