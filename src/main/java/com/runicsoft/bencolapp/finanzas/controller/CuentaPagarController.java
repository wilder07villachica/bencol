package com.runicsoft.bencolapp.finanzas.controller;

import com.runicsoft.bencolapp.finanzas.dtos.request.CuentaPagarRequest;
import com.runicsoft.bencolapp.finanzas.dtos.request.PagoProveedorRequest;
import com.runicsoft.bencolapp.finanzas.dtos.response.CuentaPagarResponse;
import com.runicsoft.bencolapp.finanzas.service.CuentaPagarService;
import com.runicsoft.bencolapp.finanzas.utils.EstadoCuentaPagar;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bencol.agua/finanzas/cuentas-pagar")
@RequiredArgsConstructor
public class CuentaPagarController {

    private final CuentaPagarService cuentaPagarService;

    @GetMapping
    public ResponseEntity<List<CuentaPagarResponse>> findAll() {
        return ResponseEntity.ok(cuentaPagarService.findAll());
    }

    @GetMapping("/{idCuenta}")
    public ResponseEntity<CuentaPagarResponse> findById(@PathVariable Long idCuenta) {
        return ResponseEntity.ok(cuentaPagarService.findById(idCuenta));
    }

    @GetMapping("/compra/{compraId}")
    public ResponseEntity<CuentaPagarResponse> findByCompraId(@PathVariable Long compraId) {
        return ResponseEntity.ok(cuentaPagarService.findByCompraId(compraId));
    }

    @GetMapping("/proveedor/{proveedorId}")
    public ResponseEntity<List<CuentaPagarResponse>> findByProveedorId(@PathVariable Long proveedorId) {
        return ResponseEntity.ok(cuentaPagarService.findByProveedorId(proveedorId));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<CuentaPagarResponse>> findByEstado(@PathVariable EstadoCuentaPagar estado) {
        return ResponseEntity.ok(cuentaPagarService.findByEstado(estado));
    }

    @PostMapping
    public ResponseEntity<CuentaPagarResponse> create(@Valid @RequestBody CuentaPagarRequest request) {
        CuentaPagarResponse response = cuentaPagarService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/pagos")
    public ResponseEntity<CuentaPagarResponse> registrarPago(@Valid @RequestBody PagoProveedorRequest request) {
        CuentaPagarResponse response = cuentaPagarService.registrarPago(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}