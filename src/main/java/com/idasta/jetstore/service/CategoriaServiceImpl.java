package com.idasta.jetstore.service;

import com.idasta.jetstore.dto.CategoriaDTO;
import com.idasta.jetstore.dto.GuardarCategoriaDTO;
import com.idasta.jetstore.model.Categoria;
import com.idasta.jetstore.repository.CategoriaRepo;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepo categoriaRepo;

    @PersistenceContext
    private EntityManager em;

    public CategoriaServiceImpl(CategoriaRepo categoriaRepo) {
        this.categoriaRepo = categoriaRepo;
    }

    @Override
    public List<CategoriaDTO> listarTodas() {
        List<Categoria> categorias = categoriaRepo.findAll();
        return categorias.stream().map(cat -> {
            Long count = em.createQuery("SELECT COUNT(l) FROM Libro l WHERE l.categoria.id = :catId", Long.class)
                    .setParameter("catId", cat.getId())
                    .getSingleResult();
            return new CategoriaDTO(cat.getId(), cat.getNombre(), cat.getDescripcion(), count);
        }).toList();
    }

    @Override
    @Transactional
    public CategoriaDTO guardar(GuardarCategoriaDTO dto) {
        if (dto.nombre() == null || dto.nombre().isBlank()) {
            throw new IllegalArgumentException("El nombre de la categoría es requerido");
        }
        String nombre = dto.nombre().trim();
        if (categoriaRepo.findByNombre(nombre).isPresent()) {
            throw new IllegalArgumentException("Ya existe una categoría con ese nombre");
        }
        Categoria cat = new Categoria();
        cat.setNombre(nombre);
        cat.setDescripcion(null);
        categoriaRepo.save(cat);
        return new CategoriaDTO(cat.getId(), cat.getNombre(), cat.getDescripcion(), 0);
    }

    @Override
    @Transactional
    public CategoriaDTO actualizar(Long id, GuardarCategoriaDTO dto) {
        Categoria cat = categoriaRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));
        if (dto.nombre() == null || dto.nombre().isBlank()) {
            throw new IllegalArgumentException("El nombre de la categoría es requerido");
        }
        String nombre = dto.nombre().trim();
        categoriaRepo.findByNombre(nombre).ifPresent(existente -> {
            if (!existente.getId().equals(id)) {
                throw new IllegalArgumentException("Ya existe una categoría con ese nombre");
            }
        });
        cat.setNombre(nombre);
        categoriaRepo.save(cat);
        Long count = em.createQuery("SELECT COUNT(l) FROM Libro l WHERE l.categoria.id = :catId", Long.class)
                .setParameter("catId", cat.getId())
                .getSingleResult();
        return new CategoriaDTO(cat.getId(), cat.getNombre(), cat.getDescripcion(), count);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Categoria cat = categoriaRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));
        Long count = em.createQuery("SELECT COUNT(l) FROM Libro l WHERE l.categoria.id = :catId", Long.class)
                .setParameter("catId", cat.getId())
                .getSingleResult();
        if (count > 0) {
            throw new IllegalArgumentException("No se puede eliminar la categoría porque tiene " + count + " libro(s) asociado(s)");
        }
        categoriaRepo.delete(cat);
    }
}
