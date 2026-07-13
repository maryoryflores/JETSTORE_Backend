package com.idasta.jetstore.service;

import com.idasta.jetstore.dto.FiltroLibroDTO;
import com.idasta.jetstore.dto.GuardarCategoriaDTO;
import com.idasta.jetstore.dto.GuardarLibroDTO;
import com.idasta.jetstore.dto.VerLibroDTO;
import com.idasta.jetstore.model.Categoria;
import com.idasta.jetstore.model.Libro;
import com.idasta.jetstore.repository.LibroRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LibroServiceImplTest {

    @Mock
    private LibroRepo repo;

    @InjectMocks
    private LibroServiceImpl service;

    @Test
    void guardar_conCategoriaNull_asignaGenerica() {
        var dto = new GuardarLibroDTO(null, "Libro", "Autor", null, BigDecimal.TEN, "PDF", null, 5);
        Categoria generica = new Categoria();
        generica.setId(1L);
        generica.setNombre("Generica");
        when(repo.CategoriaGenerica()).thenReturn(generica);
        when(repo.save(any())).thenReturn(null);

        service.guardar(dto);

        ArgumentCaptor<Libro> captor = ArgumentCaptor.forClass(Libro.class);
        verify(repo).save(captor.capture());
        assertEquals("Generica", captor.getValue().getCategoria().getNombre());
    }

    @Test
    void guardar_conCategoriaNueva_laCrea() {
        var dto = new GuardarLibroDTO(null, "Libro", "Autor", new GuardarCategoriaDTO("Nueva"), BigDecimal.TEN, "PDF", null, 5);
        when(repo.buscarCategoriaNombre("Nueva")).thenReturn(List.of());

        service.guardar(dto);

        verify(repo).crearCategoria(any());
        verify(repo).save(any());
    }

    @Test
    void guardar_conCategoriaExistente_laReusa() {
        var dto = new GuardarLibroDTO(null, "Libro", "Autor", new GuardarCategoriaDTO("Existente"), BigDecimal.TEN, "PDF", null, 5);
        Categoria existente = new Categoria();
        existente.setId(1L);
        existente.setNombre("Existente");
        when(repo.buscarCategoriaNombre("Existente")).thenReturn(List.of(existente));

        service.guardar(dto);

        verify(repo, never()).crearCategoria(any());
        ArgumentCaptor<Libro> captor = ArgumentCaptor.forClass(Libro.class);
        verify(repo).save(captor.capture());
        assertEquals(1L, captor.getValue().getCategoria().getId());
    }

    @Test
    void verLibros_delegaAlRepo() {
        var esperado = List.of(new VerLibroDTO(1L, "T", "A", "Cat", BigDecimal.TEN, "PDF", null, 5, null));
        when(repo.listarLibros()).thenReturn(esperado);

        var resultado = service.verLibros();

        assertEquals(1, resultado.size());
        assertEquals("T", resultado.get(0).titulo());
    }

    @Test
    void filtrarLibros_delegaAlRepoYTransforma() {
        Categoria cat = new Categoria();
        cat.setNombre("Ficcion");
        Libro libro = new Libro();
        libro.setId(1L);
        libro.setTitulo("Libro Filtrado");
        libro.setAutor("Autor");
        libro.setCategoria(cat);
        libro.setPrecio(BigDecimal.valueOf(30));
        libro.setFormato("PDF");
        libro.setStock(2);

        when(repo.filtrarLibros(any())).thenReturn(List.of(libro));

        var dto = new FiltroLibroDTO("Ficcion", "PDF", BigDecimal.valueOf(10), BigDecimal.valueOf(50));
        var resultado = service.filtrarLibros(dto);

        assertEquals(1, resultado.size());
        assertEquals("Libro Filtrado", resultado.get(0).titulo());
        assertEquals("Ficcion", resultado.get(0).categoria());
    }
}
