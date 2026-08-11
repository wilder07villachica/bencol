package com.runicsoft.bencolapp.caja.controller;

import com.runicsoft.bencolapp.caja.dtos.request.CajaRequest;
import com.runicsoft.bencolapp.caja.dtos.request.CierreCajaRequest;
import com.runicsoft.bencolapp.caja.dtos.request.MovimientoCajaRequest;
import com.runicsoft.bencolapp.caja.dtos.response.CajaResponse;
import com.runicsoft.bencolapp.caja.dtos.response.MovimientoCajaResponse;
import com.runicsoft.bencolapp.caja.service.CajaService;
import com.runicsoft.bencolapp.caja.utils.EstadoCaja;
import com.runicsoft.bencolapp.caja.utils.TipoMovimientoCaja;
import com.runicsoft.bencolapp.utils.pagination.PaginaResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/bencol.agua/caja")
@RequiredArgsConstructor
public class CajaController {

    private final CajaService cajaService;

    @GetMapping
    public ResponseEntity<PaginaResponse<CajaResponse>> findAll(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanio,
            @RequestParam(required = false) EstadoCaja estado,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate desde,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate hasta
    ) {
        return ResponseEntity.ok(cajaService.findAll(pagina, tamanio, estado, desde, hasta));
    }

    @GetMapping("/{idCaja}")
    public ResponseEntity<CajaResponse> findById(@PathVariable Long idCaja) {
        return ResponseEntity.ok(cajaService.findById(idCaja));
    }

    @GetMapping("/abierta")
    public ResponseEntity<CajaResponse> findCajaAbierta() {
        return ResponseEntity.ok(cajaService.findCajaAbierta());
    }

    @PostMapping("/abrir")
    public ResponseEntity<CajaResponse> abrirCaja(@Valid @RequestBody CajaRequest request) {
        CajaResponse response = cajaService.abrirCaja(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/movimientos")
    public ResponseEntity<MovimientoCajaResponse> registrarMovimiento(@Valid @RequestBody MovimientoCajaRequest request) {
        MovimientoCajaResponse response = cajaService.registrarMovimiento(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{idCaja}/cerrar")
    public ResponseEntity<CajaResponse> cerrarCaja(@PathVariable Long idCaja, @Valid @RequestBody CierreCajaRequest request) {
        CajaResponse response = cajaService.cerrarCaja(idCaja, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/movimientos")
    public ResponseEntity<PaginaResponse<MovimientoCajaResponse>> findMovimientos(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanio,
            @RequestParam(required = false) Long cajaId,
            @RequestParam(required = false) TipoMovimientoCaja tipoMovimiento,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate desde,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate hasta
    ) {
        return ResponseEntity.ok(cajaService.findMovimientos(pagina, tamanio, cajaId, tipoMovimiento, desde, hasta));
    }
}