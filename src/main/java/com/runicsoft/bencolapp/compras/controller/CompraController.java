package com.runicsoft.bencolapp.compras.controller;

import com.runicsoft.bencolapp.compras.dtos.request.CompraRequest;
import com.runicsoft.bencolapp.compras.dtos.response.CompraResponse;
import com.runicsoft.bencolapp.compras.service.CompraService;
import com.runicsoft.bencolapp.compras.utils.EstadoCompra;
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
@RequestMapping("/bencol.agua/compras")
@RequiredArgsConstructor
public class CompraController {

    private final CompraService compraService;

    @GetMapping
    public ResponseEntity<PaginaResponse<CompraResponse>> findAll(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanio,
            @RequestParam(required = false) String codigo,
            @RequestParam(required = false) Long proveedorId,
            @RequestParam(required = false) EstadoCompra estado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta
    ) {
        return ResponseEntity.ok(compraService.findAll(pagina, tamanio, codigo, proveedorId, estado, desde, hasta));
    }

    @GetMapping("/{idCompra}")
    public ResponseEntity<CompraResponse> findById(@PathVariable Long idCompra) {
        return ResponseEntity.ok(compraService.findById(idCompra));
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<CompraResponse> findByCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(compraService.findByCodigo(codigo));
    }

    @GetMapping("/proveedor/{proveedorId}")
    public ResponseEntity<List<CompraResponse>> findByProveedorId(@PathVariable Long proveedorId) {
        return ResponseEntity.ok(compraService.findByProveedorId(proveedorId));
    }

    @PostMapping
    public ResponseEntity<CompraResponse> create(@Valid @RequestBody CompraRequest request) {
        CompraResponse response = compraService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{idCompra}/anular")
    public ResponseEntity<CompraResponse> anularCompra(@PathVariable Long idCompra) {
        CompraResponse response = compraService.anularCompra(idCompra);
        return ResponseEntity.ok(response);
    }
}