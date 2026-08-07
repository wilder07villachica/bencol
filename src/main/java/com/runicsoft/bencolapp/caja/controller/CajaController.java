package com.runicsoft.bencolapp.caja.controller;

import com.runicsoft.bencolapp.caja.dtos.request.CajaRequest;
import com.runicsoft.bencolapp.caja.dtos.request.MovimientoCajaRequest;
import com.runicsoft.bencolapp.caja.dtos.response.CajaResponse;
import com.runicsoft.bencolapp.caja.dtos.response.MovimientoCajaResponse;
import com.runicsoft.bencolapp.caja.service.CajaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bencol.agua/caja")
@RequiredArgsConstructor
public class CajaController {

    private final CajaService cajaService;

    @GetMapping
    public ResponseEntity<List<CajaResponse>> findAll() {
        return ResponseEntity.ok(cajaService.findAll());
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
    public ResponseEntity<CajaResponse> cerrarCaja(@PathVariable Long idCaja) {
        CajaResponse response = cajaService.cerrarCaja(idCaja);
        return ResponseEntity.ok(response);
    }
}