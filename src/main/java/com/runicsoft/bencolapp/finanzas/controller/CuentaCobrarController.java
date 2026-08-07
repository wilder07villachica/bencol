package com.runicsoft.bencolapp.finanzas.controller;

import com.runicsoft.bencolapp.finanzas.dtos.request.CuentaCobrarRequest;
import com.runicsoft.bencolapp.finanzas.dtos.request.PagoRequest;
import com.runicsoft.bencolapp.finanzas.dtos.response.CuentaCobrarResponse;
import com.runicsoft.bencolapp.finanzas.dtos.response.DeudaClienteResponse;
import com.runicsoft.bencolapp.finanzas.dtos.response.ResumenFinancieroResponse;
import com.runicsoft.bencolapp.finanzas.service.CuentaCobrarService;
import com.runicsoft.bencolapp.finanzas.utils.EstadoCuenta;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bencol.agua/finanzas/cuentas")
@RequiredArgsConstructor
public class CuentaCobrarController {

    private final CuentaCobrarService cuentaCobrarService;

    @GetMapping
    public ResponseEntity<List<CuentaCobrarResponse>> findAll() {
        return ResponseEntity.ok(cuentaCobrarService.findAll());
    }

    @GetMapping("/{idCuenta}")
    public ResponseEntity<CuentaCobrarResponse> findById(@PathVariable Long idCuenta) {
        return ResponseEntity.ok(cuentaCobrarService.findById(idCuenta));
    }

    @GetMapping("/venta/{ventaId}")
    public ResponseEntity<CuentaCobrarResponse> findByVentaId(@PathVariable Long ventaId) {
        return ResponseEntity.ok(cuentaCobrarService.findByVentaId(ventaId));
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<CuentaCobrarResponse>> findByClienteId(@PathVariable Long clienteId) {
        return ResponseEntity.ok(cuentaCobrarService.findByClienteId(clienteId));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<CuentaCobrarResponse>> findByEstado(@PathVariable EstadoCuenta estado) {
        return ResponseEntity.ok(cuentaCobrarService.findByEstado(estado));
    }

    @PostMapping
    public ResponseEntity<CuentaCobrarResponse> create(@Valid @RequestBody CuentaCobrarRequest request) {
        CuentaCobrarResponse response = cuentaCobrarService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/pagos")
    public ResponseEntity<CuentaCobrarResponse> registrarPago(@Valid @RequestBody PagoRequest request) {
        CuentaCobrarResponse response = cuentaCobrarService.registrarPago(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/cliente/{clienteId}/deuda")
    public ResponseEntity<DeudaClienteResponse> obtenerDeudaCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(cuentaCobrarService.obtenerDeudaCliente(clienteId));
    }

    @GetMapping("/resumen")
    public ResponseEntity<ResumenFinancieroResponse> obtenerResumenFinanciero() {
        return ResponseEntity.ok(cuentaCobrarService.obtenerResumenFinanciero());
    }
}