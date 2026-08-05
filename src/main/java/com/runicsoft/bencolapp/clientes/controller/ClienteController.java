package com.runicsoft.bencolapp.clientes.controller;

import com.runicsoft.bencolapp.clientes.dtos.request.ClienteRequest;
import com.runicsoft.bencolapp.clientes.dtos.response.ClienteResponse;
import com.runicsoft.bencolapp.clientes.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bencol.agua/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @GetMapping
    public ResponseEntity<List<ClienteResponse>> findAll() {
        return ResponseEntity.ok(clienteService.findAll());
    }

    @GetMapping("/{idCliente}")
    public ResponseEntity<ClienteResponse> findById(@PathVariable Long idCliente) {
        return ResponseEntity.ok(clienteService.findById(idCliente));
    }

    @PostMapping
    public ResponseEntity<ClienteResponse> create(@Valid @RequestBody ClienteRequest request) {
        ClienteResponse response = clienteService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{idCliente}")
    public ResponseEntity<ClienteResponse> update(@Valid @PathVariable Long idCliente, @RequestBody ClienteRequest request) {
        ClienteResponse response = clienteService.update(idCliente, request);
        return ResponseEntity.ok(response);
    }
}
