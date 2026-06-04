package com.idasta.jetstore.repository;

import com.idasta.jetstore.dto.VerLibroDTO;
import com.idasta.jetstore.helper.Jetstore;
import com.idasta.jetstore.model.Categoria;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class LibroRepoImpl implements LibroRepoCustom{
    @PersistenceContext
    EntityManager em;

    @Override
    public void crearCategoria(Categoria cat) {
        if(!existeCategoria(cat.getNombre())){
            em.persist(cat);
        }
    }

    @Override
    public boolean existeCategoria(String nombre) {
        List<Categoria> existente = em.createQuery("SELECT c FROM Categoria c WHERE c.nombre = :nombre", Categoria.class)
                .setParameter("nombre", nombre)
                .getResultList();

        return !existente.isEmpty();
    }



    @Override
    public Categoria CategoriaGenerica() {
        String nombreGenerico = Jetstore.config().get("categoriaGenerica").toString();

        Categoria generic = new Categoria();
        generic.setNombre(nombreGenerico);

        if(!existeCategoria(generic.getNombre())){
            em.persist(generic);
            return generic;
        }else{
            return buscarCategoriaNombre(nombreGenerico).get(0);
        }
    }

    @Override
    public List<Categoria> buscarCategoriaNombre(String nombre) {
        return em.createQuery("SELECT c FROM Categoria c WHERE c.nombre = :nombre", Categoria.class)
                .setParameter("nombre", nombre)
                .getResultList();
    }

    @Override
    public List<VerLibroDTO> listarLibros() {
        return em.createQuery("SELECT new com.idasta.jetstore.dto.VerLibroDTO(l.id, l.titulo, l.autor, l.categoria.nombre, l.precio, l.formato, l.stock) FROM Libro l", VerLibroDTO.class)
                .getResultList();
    }
}
