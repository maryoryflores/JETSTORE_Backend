package com.idasta.jetstore.controller;

import com.idasta.jetstore.dto.GuardarLibroDTO;
import com.idasta.jetstore.helper.RespuestaApi;
import com.idasta.jetstore.model.Libro;
import com.idasta.jetstore.service.LibroService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("libro/")
public class LibroController {
    private final LibroService service;
    public LibroController(LibroService service){
        this.service = service;
    }

    @PostMapping("guardar-nuevo-libro/")
    public ResponseEntity<?> guardarNuevoLibro(@RequestBody GuardarLibroDTO dto){
        service.guardar(dto);
        return ResponseEntity.ok(new RespuestaApi(true, "Libro subido y guardado correctamente", null));
    }

    @GetMapping("ver-libros/")
    public ResponseEntity<?> verLibros(){
        return ResponseEntity.ok(new RespuestaApi(true, "Se ha cargado la lista de libros", service.verLibros()));
    }
}
