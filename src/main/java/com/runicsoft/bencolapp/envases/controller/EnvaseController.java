package com.runicsoft.bencolapp.envases.controller;

import com.runicsoft.bencolapp.envases.dtos.request.MovimientoEnvaseRequest;
import com.runicsoft.bencolapp.envases.dtos.request.SaldoInicialEnvaseRequest;
import com.runicsoft.bencolapp.envases.dtos.response.CuentaEnvasesClienteResponse;
import com.runicsoft.bencolapp.envases.dtos.response.MovimientoEnvaseResponse;
import com.runicsoft.bencolapp.envases.service.EnvaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bencol.agua/envases")
@RequiredArgsConstructor
public class EnvaseController {

    private final EnvaseService envaseService;

    @GetMapping
    public ResponseEntity<List<CuentaEnvasesClienteResponse>> findAll() {
        return ResponseEntity.ok(envaseService.findAll());
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<CuentaEnvasesClienteResponse>> findByClienteId(@PathVariable Long clienteId) {
        return ResponseEntity.ok(envaseService.findByClienteId(clienteId));
    }

    @GetMapping("/cliente/{clienteId}/producto/{productoId}")
    public ResponseEntity<CuentaEnvasesClienteResponse> findByClienteAndProducto(
            @PathVariable Long clienteId,
            @PathVariable Long productoId
    ) {
        return ResponseEntity.ok(envaseService.findByClienteAndProducto(clienteId, productoId));
    }

    @GetMapping("/{cuentaId}/movimientos")
    public ResponseEntity<List<MovimientoEnvaseResponse>> findMovimientosByCuentaId(@PathVariable Long cuentaId) {
        return ResponseEntity.ok(envaseService.findMovimientosByCuentaId(cuentaId));
    }

    @PostMapping("/movimientos")
    public ResponseEntity<CuentaEnvasesClienteResponse> registrarMovimiento(@Valid @RequestBody MovimientoEnvaseRequest request) {
        CuentaEnvasesClienteResponse response = envaseService.registrarMovimiento(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/saldo-inicial")
    public ResponseEntity<CuentaEnvasesClienteResponse> registrarSaldoInicial(@Valid @RequestBody SaldoInicialEnvaseRequest request) {
        CuentaEnvasesClienteResponse response = envaseService.registrarSaldoInicial(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}