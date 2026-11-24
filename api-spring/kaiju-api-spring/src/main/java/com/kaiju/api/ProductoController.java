package com.kaiju.api;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoRepository productoRepository;
    private final ProductoService productoService;

    public ProductoController(
            ProductoRepository productoRepository,
            ProductoService productoService
    ) {
        this.productoRepository = productoRepository;
        this.productoService = productoService;
    }

    @GetMapping
    public List<Producto> listar() {
        return productoRepository.findAll(
                Sort.by(Sort.Direction.ASC, "codigo")
        );
    }

    @GetMapping("/{codigo}")
    public ResponseEntity<Producto> obtenerPorCodigo(
            @PathVariable String codigo
    ) {
        return productoRepository.findById(codigo)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Producto> crear(
            @Valid @RequestBody ProductoRequest solicitud
    ) {
        Producto producto = productoService.crear(solicitud);
        return ResponseEntity.status(HttpStatus.CREATED).body(producto);
    }
}