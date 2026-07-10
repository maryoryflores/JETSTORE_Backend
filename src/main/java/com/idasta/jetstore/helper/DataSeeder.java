package com.idasta.jetstore.helper;

import com.idasta.jetstore.model.Categoria;
import com.idasta.jetstore.model.Libro;
import com.idasta.jetstore.model.Rol;
import com.idasta.jetstore.model.Usuario;
import com.idasta.jetstore.repository.CategoriaRepo;
import com.idasta.jetstore.repository.LibroRepo;
import com.idasta.jetstore.repository.RolRepo;
import com.idasta.jetstore.repository.UsuarioRepo;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Component
public class DataSeeder {
    private final RolRepo rolRepo;
    private final UsuarioRepo usuarioRepo;
    private final CategoriaRepo categoriaRepo;
    private final LibroRepo libroRepo;

    public DataSeeder(RolRepo rolRepo, UsuarioRepo usuarioRepo, CategoriaRepo categoriaRepo, LibroRepo libroRepo) {
        this.rolRepo = rolRepo;
        this.usuarioRepo = usuarioRepo;
        this.categoriaRepo = categoriaRepo;
        this.libroRepo = libroRepo;
    }

    @PostConstruct
    public void seed() {
        seedRoles();
        seedUsers();
        seedCategorias();
        seedLibros();
    }

    private void seedRoles() {
        if (rolRepo.count() == 0) {
            Rol admin = new Rol();
            admin.setNombre("ADMIN");
            rolRepo.save(admin);

            Rol cliente = new Rol();
            cliente.setNombre("CLIENTE");
            rolRepo.save(cliente);
        }
    }

    private void seedUsers() {
        if (usuarioRepo.count() == 0) {
            Rol adminRol = rolRepo.findByNombre("ADMIN")
                    .orElseThrow(() -> new RuntimeException("Rol ADMIN no encontrado"));

            Usuario adminUser = new Usuario();
            adminUser.setNombreUsuario("admin");
            adminUser.setCorreo("admin@jetstore.pe");
            adminUser.setPasswordHash(PasswordUtil.hash("admin123"));
            adminUser.setRol(adminRol);
            usuarioRepo.save(adminUser);

            Rol clienteRol = rolRepo.findByNombre("CLIENTE")
                    .orElseThrow(() -> new RuntimeException("Rol CLIENTE no encontrado"));

            Usuario clienteUser = new Usuario();
            clienteUser.setNombreUsuario("invitado");
            clienteUser.setCorreo("invitado@jetstore.pe");
            clienteUser.setPasswordHash(PasswordUtil.hash("invitado123"));
            clienteUser.setRol(clienteRol);
            usuarioRepo.save(clienteUser);
        }
    }

    private void seedCategorias() {
        if (categoriaRepo.count() == 0) {
            String[] categorias = {"Tecnología", "Negocios", "Salud", "Educación", "Diseño", "Marketing"};
            for (String nombre : categorias) {
                Categoria cat = new Categoria();
                cat.setNombre(nombre);
                categoriaRepo.save(cat);
            }
        }
    }

    private void seedLibros() {
        if (libroRepo.count() == 0) {
            Map<String, Categoria> cats = Map.of(
                    "Tecnología", categoriaRepo.findByNombre("Tecnología").orElseThrow(),
                    "Negocios",   categoriaRepo.findByNombre("Negocios").orElseThrow(),
                    "Salud",      categoriaRepo.findByNombre("Salud").orElseThrow(),
                    "Educación",  categoriaRepo.findByNombre("Educación").orElseThrow(),
                    "Diseño",     categoriaRepo.findByNombre("Diseño").orElseThrow(),
                    "Marketing",  categoriaRepo.findByNombre("Marketing").orElseThrow()
            );

            Object[][] libros = {
                    {"Manual de Enfermería Integral",           "Dr. Carlos Mendoza",       cats.get("Salud"),      new BigDecimal("25.00"), "PDF", "/productos/enfermeriaa.jpg",          10},
                    {"Programación Web Moderna con React y Next.js", "Ing. Ana Torres",     cats.get("Tecnología"), new BigDecimal("30.00"), "PDF", "/productos/programacion-webb.jpg",    10},
                    {"Marketing Digital 360",                   "MBA. Luis García",         cats.get("Marketing"),  new BigDecimal("20.00"), "PDF", "/productos/marketingg.jpg",           10},
                    {"Inteligencia Artificial Aplicada",        "PhD. María Rodríguez",     cats.get("Tecnología"), new BigDecimal("35.00"), "PDF", "/productos/iaa.jpg",                  10},
                    {"Finanzas Personales para Profesionales",  "CPA. Roberto Silva",       cats.get("Negocios"),   new BigDecimal("18.00"), "PDF", "/productos/finanzass.jpg",            10},
                    {"Diseño UX/UI: De Principiante a Experto", "Dis. Laura Castillo",      cats.get("Diseño"),     new BigDecimal("22.00"), "PDF", "/productos/ux-uii.jpg",               10},
                    {"Pedagogía Moderna: Estrategias de Enseñanza", "Dra. Patricia Núñez", cats.get("Educación"),  new BigDecimal("16.00"), "PDF", "/productos/pedagogiaa.jpg",           10},
                    {"Ciberseguridad: Protección de Datos",    "Ing. Miguel Ángel Ruiz",   cats.get("Tecnología"), new BigDecimal("28.00"), "PDF", "/productos/ciberseguridadd.jpg",      10},
                    {"Liderazgo y Gestión de Equipos",         "Mg. Fernando Paz",         cats.get("Negocios"),   new BigDecimal("24.00"), "PDF", "/productos/liderazgoo.jpg",           10},
                    {"Nutrición y Dietética Clínica",          "Lic. Carmen Flores",       cats.get("Salud"),      new BigDecimal("26.00"), "PDF", "/productos/nutricionn.jpg",           10},
                    {"Cloud Computing con AWS",                 "Ing. Diego Ramos",         cats.get("Tecnología"), new BigDecimal("32.00"), "PDF", "/productos/awss.jpg",                 10},
                    {"Redes Sociales: Estrategia y Gestión",   "Lic. Sofía Martínez",      cats.get("Marketing"),  new BigDecimal("19.00"), "PDF", "/productos/redes-socialess.jpg",      10},
                    {"Farmacología Esencial",                  "Dr. Ricardo Vega",         cats.get("Salud"),      new BigDecimal("29.00"), "PDF", "/productos/farmacologiaa.jpg",        10},
                    {"Metodología de la Investigación Científica", "Dr. José Luis Campos", cats.get("Educación"),  new BigDecimal("21.00"), "PDF", "/productos/investigacionn.jpg",       10},
                    {"Ilustración Digital con Procreate",      "Dis. Andrea Gutiérrez",    cats.get("Diseño"),     new BigDecimal("23.00"), "PDF", "/productos/ilustracionn.jpg",         10},
            };

            for (Object[] l : libros) {
                Libro libro = new Libro();
                libro.setTitulo((String) l[0]);
                libro.setAutor((String) l[1]);
                libro.setCategoria((Categoria) l[2]);
                libro.setPrecio((BigDecimal) l[3]);
                libro.setFormato((String) l[4]);
                libro.setImagen((String) l[5]);
                libro.setStock((int) l[6]);
                libroRepo.save(libro);
            }
        }
    }
}
