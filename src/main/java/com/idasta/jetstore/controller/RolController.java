package com.idasta.jetstore.controller;

import com.idasta.jetstore.helper.RespuestaApi;
import com.idasta.jetstore.model.Rol;
import com.idasta.jetstore.repository.RolRepo;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("rol/")
public class RolController {
    private final RolRepo rolRepo;

    public RolController(RolRepo rolRepo) {
        this.rolRepo = rolRepo;
    }

    @PostMapping("crear")
    public ResponseEntity<?> crear(@RequestBody Map<String, String> body) {
        String nombre = body.get("nombre");
        if (nombre == null || nombre.isBlank()) {
            return ResponseEntity.badRequest()
                    .body(new RespuestaApi(false, "El nombre del rol es requerido", null));
        }
        if (rolRepo.findByNombre(nombre).isPresent()) {
            return ResponseEntity.badRequest()
                    .body(new RespuestaApi(false, "El rol ya existe", null));
        }
        Rol rol = new Rol();
        rol.setNombre(nombre);
        rolRepo.save(rol);
        return ResponseEntity.ok(new RespuestaApi(true, "Rol creado correctamente", null));
    }

    @GetMapping("todos")
    public ResponseEntity<?> todos() {
        return ResponseEntity.ok(new RespuestaApi(true, "Lista de roles", rolRepo.findAll()));
    }
}
