package com.idasta.jetstore.service;

import com.idasta.jetstore.dto.AgregarAlCarritoDTO;
import com.idasta.jetstore.dto.CarritoResponseDTO;
import com.idasta.jetstore.dto.ItemCarritoDTO;
import com.idasta.jetstore.model.Carrito;
import com.idasta.jetstore.model.Libro;
import com.idasta.jetstore.model.Usuario;
import com.idasta.jetstore.repository.CarritoRepo;
import com.idasta.jetstore.repository.LibroRepo;
import com.idasta.jetstore.repository.UsuarioRepo;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CarritoServiceImpl implements CarritoService {
    private final CarritoRepo carritoRepo;
    private final LibroRepo libroRepo;
    private final UsuarioRepo usuarioRepo;

    public CarritoServiceImpl(CarritoRepo carritoRepo, LibroRepo libroRepo, UsuarioRepo usuarioRepo) {
        this.carritoRepo = carritoRepo;
        this.libroRepo = libroRepo;
        this.usuarioRepo = usuarioRepo;
    }

    @Transactional
    @Override
    public CarritoResponseDTO agregar(AgregarAlCarritoDTO dto) {
        Usuario usuario = usuarioRepo.findById(dto.usuarioId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Carrito carrito = carritoRepo.findByUsuarioId(dto.usuarioId())
                .orElseGet(() -> {
                    Carrito nuevo = new Carrito();
                    nuevo.setUsuario(usuario);
                    nuevo.setFechaCreacion(LocalDateTime.now());
                    return carritoRepo.save(nuevo);
                });

        Libro libro = libroRepo.findById(dto.libroId())
                .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado"));

        carrito.agregarOActualizarItem(libro, dto.cantidad());
        carritoRepo.save(carrito);

        return toResponse(carrito);
    }

    @Override
    public CarritoResponseDTO ver(Long usuarioId) {
        Carrito carrito = carritoRepo.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("El carrito esta vacio"));
        return toResponse(carrito);
    }

    @Transactional
    @Override
    public CarritoResponseDTO eliminarItem(Long usuarioId, Long libroId) {
        Carrito carrito = carritoRepo.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Carrito no encontrado"));
        carrito.eliminarItem(libroId);
        carritoRepo.save(carrito);
        return toResponse(carrito);
    }

    private CarritoResponseDTO toResponse(Carrito carrito) {
        List<ItemCarritoDTO> items = carrito.getItems().stream()
                .map(item -> new ItemCarritoDTO(
                        item.getId(),
                        item.getLibro().getId(),
                        item.getLibro().getTitulo(),
                        item.getLibro().getAutor(),
                        item.getLibro().getPrecio(),
                        item.getCantidad(),
                        item.getSubtotal()))
                .toList();
        return new CarritoResponseDTO(carrito.getId(), items, carrito.getTotal());
    }
}
