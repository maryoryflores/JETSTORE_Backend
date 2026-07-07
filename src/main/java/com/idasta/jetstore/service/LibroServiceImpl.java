package com.idasta.jetstore.service;

import com.idasta.jetstore.dto.AgregarStockDTO;
import com.idasta.jetstore.dto.FiltroLibroDTO;
import com.idasta.jetstore.dto.GuardarLibroDTO;
import com.idasta.jetstore.dto.VerLibroDTO;
import com.idasta.jetstore.helper.Mapear;
import com.idasta.jetstore.model.Categoria;
import com.idasta.jetstore.model.Libro;
import com.idasta.jetstore.repository.LibroRepo;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LibroServiceImpl implements LibroService{
    private final LibroRepo repo;

    public LibroServiceImpl(LibroRepo repo){
        this.repo = repo;
    }

    @Transactional
    @Override
    public void guardar(GuardarLibroDTO dto) {
        Libro libro = Mapear.libro(dto);
        if(dto.categoria()==null){
            libro.setCategoria(repo.CategoriaGenerica());
        }else {
            String nombreCat = dto.categoria().nombre();
            List<Categoria> existentes = repo.buscarCategoriaNombre(nombreCat);
            Categoria cat;
            if(existentes.isEmpty()){
                cat = Mapear.categoria(dto.categoria());
                repo.crearCategoria(cat);
            }else{
                cat = existentes.get(0);
            }
            libro.setCategoria(cat);
        }

        repo.save(libro);
    }

    @Transactional
    @Override
    public void agregarStock(AgregarStockDTO dto) {
        Libro libro = repo.findById(dto.libroId())
                .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado"));
        libro.setStock(libro.getStock() + dto.cantidad());
        repo.save(libro);
    }

    @Transactional
    @Override
    public void eliminar(Long libroId) {
        if(!repo.existsById(libroId)){
            throw new IllegalArgumentException("Libro no encontrado");
        }
        repo.deleteById(libroId);
    }

    @Override
    public List<VerLibroDTO> filtrarLibros(FiltroLibroDTO dto) {
        return repo.filtrarLibros(dto).stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    public List<VerLibroDTO> verLibros() {
        return repo.listarLibros();
    }

    @Override
    public List<VerLibroDTO> buscar(String keyword) {
        return repo.buscarPorKeyword(keyword).stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    public List<VerLibroDTO> listarPorCategoria(String categoria) {
        return repo.listarPorCategoria(categoria).stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    public List<VerLibroDTO> listarMasRecientes() {
        return repo.listarMasRecientes().stream()
                .map(this::toDTO)
                .toList();
    }

    private VerLibroDTO toDTO(Libro l) {
        return new VerLibroDTO(
                l.getId(), l.getTitulo(), l.getAutor(),
                l.getCategoria().getNombre(), l.getPrecio(),
                l.getFormato(), l.getStock(), l.getFechaCreacion());
    }
}
