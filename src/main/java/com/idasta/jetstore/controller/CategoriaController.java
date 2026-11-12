package com.idasta.jetstore.controller;

import com.idasta.jetstore.dto.GuardarCategoriaDTO;
import com.idasta.jetstore.helper.RespuestaApi;
import com.idasta.jetstore.service.CategoriaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/categoria")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @GetMapping("/todos")
    public ResponseEntity<?> listar() {
        return ResponseEntity.ok(new RespuestaApi(true, "Lista de categorías", categoriaService.listarTodas()));
    }

    @PostMapping("/guardar")
    public ResponseEntity<?> guardar(@RequestBody GuardarCategoriaDTO dto) {
        var creada = categoriaService.guardar(dto);
        return ResponseEntity.ok(new RespuestaApi(true, "Categoría creada correctamente", creada));
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody GuardarCategoriaDTO dto) {
        var actualizada = categoriaService.actualizar(id, dto);
        return ResponseEntity.ok(new RespuestaApi(true, "Categoría actualizada correctamente", actualizada));
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        categoriaService.eliminar(id);
        return ResponseEntity.ok(new RespuestaApi(true, "Categoría eliminada correctamente", null));
    }
}
