package com.runicsoft.bencolapp.utils.pagination;

import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
public class PaginaResponse<T> {

    private List<T> contenido;
    private int pagina;
    private int tamanio;
    private long totalElementos;
    private int totalPaginas;
    private boolean primera;
    private boolean ultima;

    public static <T> PaginaResponse<T> from(Page<T> page) {
        PaginaResponse<T> response = new PaginaResponse<>();
        response.setContenido(page.getContent());
        response.setPagina(page.getNumber());
        response.setTamanio(page.getSize());
        response.setTotalElementos(page.getTotalElements());
        response.setTotalPaginas(page.getTotalPages());
        response.setPrimera(page.isFirst());
        response.setUltima(page.isLast());
        return response;
    }
}