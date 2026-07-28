package com.runicsoft.bencolapp.clientes.controller;

import com.runicsoft.bencolapp.clientes.dtos.request.ClienteRequest;
import com.runicsoft.bencolapp.clientes.dtos.response.ClienteResponse;
import com.runicsoft.bencolapp.clientes.service.ClienteService;
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
    public ResponseEntity<List<ClienteResponse>> listarClientes() {
        return ResponseEntity.ok(clienteService.listarClientes());
    }

    @GetMapping("/{idCliente}")
    public ResponseEntity<ClienteResponse> buscarClientePorId(@PathVariable Long idCliente) {
        return ResponseEntity.ok(clienteService.buscarClientePorId(idCliente));
    }

    @PostMapping
    public ResponseEntity<ClienteResponse> registrarCliente(@RequestBody ClienteRequest request) {
        ClienteResponse response = clienteService.registrarCliente(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{idCliente}")
    public ResponseEntity<ClienteResponse> actualizarCliente(@PathVariable Long idCliente, @RequestBody ClienteRequest request) {
        ClienteResponse response = clienteService.actualizarCliente(idCliente, request);
        return ResponseEntity.ok(response);
    }
}
