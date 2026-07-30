package com.runicsoft.bencolapp.precios_clientes.controller;

import com.runicsoft.bencolapp.precios_clientes.dtos.request.ClientePrecioRequest;
import com.runicsoft.bencolapp.precios_clientes.dtos.response.ClientePrecioResponse;
import com.runicsoft.bencolapp.precios_clientes.service.ClientePrecioService;
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
    public ResponseEntity<List<ClientePrecioResponse>> listarPreciosClientes() {
        return ResponseEntity.ok(clientePrecioService.listarPreciosClientes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientePrecioResponse> buscarPrecioPorId(@PathVariable Long id) {
        return ResponseEntity.ok(clientePrecioService.buscarPrecioPorId(id));
    }

    @PostMapping
    public ResponseEntity<ClientePrecioResponse> registrarNuevoPrecio(@RequestBody ClientePrecioRequest request) {
        ClientePrecioResponse response = clientePrecioService.registrarNuevoPrecio(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClientePrecioResponse> actualizarPrecio(@PathVariable Long id, @RequestBody ClientePrecioRequest request) {
        return ResponseEntity.ok(clientePrecioService.actualizarInformacion(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPrecio(@PathVariable Long id) {
        clientePrecioService.deleteClientePrecio(id);
        return ResponseEntity.noContent().build();
    }
}
