package com.runicsoft.bencolapp.clientes.controller;

import com.runicsoft.bencolapp.clientes.dtos.request.ClienteRequest;
import com.runicsoft.bencolapp.clientes.dtos.response.ClienteResponse;
import com.runicsoft.bencolapp.clientes.service.ClienteService;
import com.runicsoft.bencolapp.clientes.utils.CategoriaCliente;
import com.runicsoft.bencolapp.utils.EstadoGeneral;
import com.runicsoft.bencolapp.utils.pagination.PaginaResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/bencol.agua/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;

    @GetMapping
    public ResponseEntity<PaginaResponse<ClienteResponse>> findAll(
            @RequestParam(defaultValue = "0") int pagina,
            @RequestParam(defaultValue = "20") int tamanio,
            @RequestParam(required = false) String texto,
            @RequestParam(required = false) EstadoGeneral estado,
            @RequestParam(required = false) CategoriaCliente categoria
    ) {
        return ResponseEntity.ok(
                clienteService.findAll(pagina, tamanio, texto, estado, categoria)
        );
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
