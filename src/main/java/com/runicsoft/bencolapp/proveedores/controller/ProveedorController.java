package com.runicsoft.bencolapp.proveedores.controller;

import com.runicsoft.bencolapp.proveedores.dtos.request.ProveedorRequest;
import com.runicsoft.bencolapp.proveedores.dtos.response.ProveedorResponse;
import com.runicsoft.bencolapp.proveedores.service.ProveedorService;
import com.runicsoft.bencolapp.utils.EstadoGeneral;
import com.runicsoft.bencolapp.utils.pagination.PaginaResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bencol.agua/proveedores")
@RequiredArgsConstructor
public class ProveedorController {

    private final ProveedorService proveedorService;

    @GetMapping
    public ResponseEntity<PaginaResponse<ProveedorResponse>> findAll(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanio,
            @RequestParam(required = false) String texto,
            @RequestParam(required = false) EstadoGeneral estado
    ) {
        return ResponseEntity.ok(proveedorService.findAll(pagina, tamanio, texto, estado));
    }

    @GetMapping("/{idProveedor}")
    public ResponseEntity<ProveedorResponse> findById(@PathVariable Long idProveedor) {
        return ResponseEntity.ok(proveedorService.findById(idProveedor));
    }

    @GetMapping("/ruc/{ruc}")
    public ResponseEntity<ProveedorResponse> findByRuc(@PathVariable String ruc) {
        return ResponseEntity.ok(proveedorService.findByRuc(ruc));
    }

    @PostMapping
    public ResponseEntity<ProveedorResponse> create(@Valid @RequestBody ProveedorRequest request) {
        ProveedorResponse response = proveedorService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{idProveedor}")
    public ResponseEntity<ProveedorResponse> update(@PathVariable Long idProveedor, @Valid @RequestBody ProveedorRequest request) {
        ProveedorResponse response = proveedorService.update(idProveedor, request);
        return ResponseEntity.ok(response);
    }
}