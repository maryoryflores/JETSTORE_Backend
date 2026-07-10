package com.idasta.jetstore.repository;

import com.idasta.jetstore.dto.FiltroLibroDTO;
import com.idasta.jetstore.dto.VerLibroDTO;
import com.idasta.jetstore.helper.Jetstore;
import com.idasta.jetstore.model.Categoria;
import com.idasta.jetstore.model.Libro;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public List<Categoria> listarCategorias() {
        return em.createQuery("SELECT c FROM Categoria c ORDER BY c.nombre ASC", Categoria.class)
                .getResultList();
    }

    @Override
    public VerLibroDTO obtenerLibroPorId(Long id) {
        List<VerLibroDTO> result = em.createQuery(
                "SELECT new com.idasta.jetstore.dto.VerLibroDTO(l.id, l.titulo, l.autor, l.categoria.nombre, l.precio, l.formato, l.imagen, l.stock, l.fechaCreacion) FROM Libro l WHERE l.id = :id",
                VerLibroDTO.class)
                .setParameter("id", id)
                .getResultList();
        return result.isEmpty() ? null : result.get(0);
    }

    @Override
    public List<VerLibroDTO> listarLibros() {
        return em.createQuery("SELECT new com.idasta.jetstore.dto.VerLibroDTO(l.id, l.titulo, l.autor, l.categoria.nombre, l.precio, l.formato, l.imagen, l.stock, l.fechaCreacion) FROM Libro l ORDER BY l.fechaCreacion DESC", VerLibroDTO.class)
                .getResultList();
    }

    @Override
    public List<Libro> filtrarLibros(FiltroLibroDTO dto) {
        StringBuilder jpql = new StringBuilder("SELECT l FROM Libro l WHERE 1=1");
        Map<String, Object> parametros = new HashMap<>();

        if(dto.categoria() != null){
            jpql.append(" AND l.categoria.nombre = :categoria");
            parametros.put("categoria", dto.categoria());
        }
        if(dto.formato() != null){
            jpql.append(" AND l.formato = :formato");
            parametros.put("formato", dto.formato());
        }
        if(dto.precioDesde() != null){
            jpql.append(" AND l.precio >= :precioDesde");
            parametros.put("precioDesde", dto.precioDesde());
        }
        if(dto.precioHasta() != null){
            jpql.append(" AND l.precio <= :precioHasta");
            parametros.put("precioHasta", dto.precioHasta());
        }

        var query = em.createQuery(jpql.toString(), Libro.class);
        for(var entry : parametros.entrySet()){
            query.setParameter(entry.getKey(), entry.getValue());
        }
        return query.getResultList();
    }

    @Override
    public List<Libro> buscarPorKeyword(String keyword) {
        return em.createQuery(
                "SELECT l FROM Libro l WHERE LOWER(l.titulo) LIKE LOWER(:kw) OR LOWER(l.autor) LIKE LOWER(:kw) ORDER BY l.fechaCreacion DESC",
                Libro.class)
                .setParameter("kw", "%" + keyword + "%")
                .getResultList();
    }

    @Override
    public List<Libro> listarPorCategoria(String categoria) {
        return em.createQuery(
                "SELECT l FROM Libro l WHERE l.categoria.nombre = :cat ORDER BY l.fechaCreacion DESC",
                Libro.class)
                .setParameter("cat", categoria)
                .getResultList();
    }

    @Override
    public List<Libro> listarMasRecientes() {
        return em.createQuery(
                "SELECT l FROM Libro l ORDER BY l.fechaCreacion DESC",
                Libro.class)
                .setMaxResults(20)
                .getResultList();
    }
}
