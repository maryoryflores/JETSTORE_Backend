package com.idasta.jetstore.controller;

import com.idasta.jetstore.dto.AgregarStockDTO;
import com.idasta.jetstore.dto.FiltroLibroDTO;
import com.idasta.jetstore.dto.GuardarLibroDTO;
import com.idasta.jetstore.helper.RespuestaApi;
import com.idasta.jetstore.service.LibroService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("libro/")
public class LibroController {
    private final LibroService service;

    public LibroController(LibroService service){
        this.service = service;
    }

    @PostMapping("guardar")
    public ResponseEntity<?> guardar(@RequestBody GuardarLibroDTO dto){
        service.guardar(dto);
        return ResponseEntity.ok(new RespuestaApi(true, "Libro guardado correctamente", null));
    }

    @PostMapping("agregar-stock")
    public ResponseEntity<?> agregarStock(@RequestBody AgregarStockDTO dto){
        service.agregarStock(dto);
        return ResponseEntity.ok(new RespuestaApi(true, "Stock actualizado correctamente", null));
    }

    @DeleteMapping("eliminar/{libroId}")
    public ResponseEntity<?> eliminar(@PathVariable Long libroId){
        service.eliminar(libroId);
        return ResponseEntity.ok(new RespuestaApi(true, "Libro eliminado", null));
    }

    @GetMapping("todos")
    public ResponseEntity<?> verLibros(){
        return ResponseEntity.ok(new RespuestaApi(true, "Lista de libros", service.verLibros()));
    }

    @GetMapping("buscar")
    public ResponseEntity<?> buscar(@RequestParam String q){
        return ResponseEntity.ok(new RespuestaApi(true, "Resultados de busqueda", service.buscar(q)));
    }

    @PostMapping("filtrar")
    public ResponseEntity<?> filtrarLibros(@RequestBody FiltroLibroDTO dto){
        return ResponseEntity.ok(new RespuestaApi(true, "Libros filtrados", service.filtrarLibros(dto)));
    }

    @GetMapping("categoria/{nombre}")
    public ResponseEntity<?> porCategoria(@PathVariable String nombre){
        return ResponseEntity.ok(new RespuestaApi(true, "Libros por categoria", service.listarPorCategoria(nombre)));
    }

    @GetMapping("recientes")
    public ResponseEntity<?> masRecientes(){
        return ResponseEntity.ok(new RespuestaApi(true, "Libros mas recientes", service.listarMasRecientes()));
    }
}
