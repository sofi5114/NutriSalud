package pe.upn.nutrisalud.dto;

import java.util.List;

import org.springframework.data.domain.Page;

/**
 * Una página de resultados (Semana 11). Mismos nombres que la interfaz Pagina<T> de Angular.
 * No se devuelve el Page de Spring: trae muchos campos internos que el cliente no necesita.
 */
public record Pagina<T>(List<T> contenido, int pagina, int tamano, long totalElementos, int totalPaginas) {

    public static <T> Pagina<T> de(Page<T> page) {
        return new Pagina<>(page.getContent(), page.getNumber(), page.getSize(),
                page.getTotalElements(), page.getTotalPages());
    }
}
