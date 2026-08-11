package com.runicsoft.bencolapp.finanzas.controller;

import com.runicsoft.bencolapp.finanzas.dtos.request.CuentaPagarRequest;
import com.runicsoft.bencolapp.finanzas.dtos.request.PagoProveedorRequest;
import com.runicsoft.bencolapp.finanzas.dtos.response.CuentaPagarResponse;
import com.runicsoft.bencolapp.finanzas.dtos.response.DeudaProveedorResponse;
import com.runicsoft.bencolapp.finanzas.dtos.response.ResumenCuentasPagarResponse;
import com.runicsoft.bencolapp.finanzas.service.CuentaPagarService;
import com.runicsoft.bencolapp.finanzas.utils.EstadoCuentaPagar;
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
@RequestMapping("/bencol.agua/finanzas/cuentas-pagar")
@RequiredArgsConstructor
public class CuentaPagarController {

    private final CuentaPagarService cuentaPagarService;

    @GetMapping
    public ResponseEntity<PaginaResponse<CuentaPagarResponse>> findAll(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanio,
            @RequestParam(required = false) Long proveedorId,
            @RequestParam(required = false) EstadoCuentaPagar estado,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate desde,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate hasta
    ) {
        return ResponseEntity.ok(cuentaPagarService.findAll(pagina, tamanio, proveedorId, estado, desde, hasta));
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

    @GetMapping("/proveedor/{proveedorId}/deuda")
    public ResponseEntity<DeudaProveedorResponse> obtenerDeudaProveedor(@PathVariable Long proveedorId) {
        return ResponseEntity.ok(cuentaPagarService.obtenerDeudaProveedor(proveedorId));
    }

    @GetMapping("/resumen")
    public ResponseEntity<ResumenCuentasPagarResponse> obtenerResumenCuentasPagar() {
        return ResponseEntity.ok(cuentaPagarService.obtenerResumenCuentasPagar());
    }
}