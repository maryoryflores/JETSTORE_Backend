package com.idasta.jetstore.controller;

import com.idasta.jetstore.dto.AgregarAlCarritoDTO;
import com.idasta.jetstore.helper.RespuestaApi;
import com.idasta.jetstore.service.CarritoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("carrito/")
public class CarritoController {
    private final CarritoService carritoService;

    public CarritoController(CarritoService carritoService) {
        this.carritoService = carritoService;
    }

    @PostMapping("agregar")
    public ResponseEntity<?> agregarAlCarrito(@RequestBody AgregarAlCarritoDTO dto) {
        var carrito = carritoService.agregar(dto);
        return ResponseEntity.ok(new RespuestaApi(true, "Libro agregado al carrito", carrito));
    }

    @GetMapping("ver/{usuarioId}")
    public ResponseEntity<?> verCarrito(@PathVariable Long usuarioId) {
        var carrito = carritoService.ver(usuarioId);
        return ResponseEntity.ok(new RespuestaApi(true, "Carrito obtenido", carrito));
    }

    @DeleteMapping("eliminar/{usuarioId}/{libroId}")
    public ResponseEntity<?> eliminarDelCarrito(@PathVariable Long usuarioId, @PathVariable Long libroId) {
        var carrito = carritoService.eliminarItem(usuarioId, libroId);
        return ResponseEntity.ok(new RespuestaApi(true, "Libro eliminado del carrito", carrito));
    }
}
