package com.runicsoft.bencolapp.precios_clientes.controller;

import com.runicsoft.bencolapp.precios_clientes.dtos.request.ClientePrecioRequest;
import com.runicsoft.bencolapp.precios_clientes.dtos.response.ClientePrecioResponse;
import com.runicsoft.bencolapp.precios_clientes.service.ClientePrecioService;
import com.runicsoft.bencolapp.utils.pagination.PaginaResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bencol.agua/precios-clientes")
@RequiredArgsConstructor
public class ClientePrecioController {

    private final ClientePrecioService clientePrecioService;

    @GetMapping
    public ResponseEntity<PaginaResponse<ClientePrecioResponse>> findAll(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanio,
            @RequestParam(required = false) Long clienteId,
            @RequestParam(required = false) Long productoId
    ) {
        return ResponseEntity.ok(clientePrecioService.findAll(pagina, tamanio, clienteId, productoId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientePrecioResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(clientePrecioService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ClientePrecioResponse> create(@Valid @RequestBody ClientePrecioRequest request) {
        ClientePrecioResponse response = clientePrecioService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientePrecioResponse> update(@PathVariable Long id, @Valid @RequestBody ClientePrecioRequest request) {
        return ResponseEntity.ok(clientePrecioService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        clientePrecioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
