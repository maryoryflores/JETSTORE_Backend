package com.idasta.jetstore.service;

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
            Categoria cat = Mapear.categoria(dto.categoria());
            if(!repo.existeCategoria(cat.getNombre())){
                repo.crearCategoria(cat);
            }
            libro.setCategoria(cat);
        }

        repo.save(libro);
    }

    @Override
    public List<VerLibroDTO> verLibros() {
        return repo.listarLibros();
    }
}
