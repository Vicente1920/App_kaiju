package com.kaiju.api;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/movimientos")
public class MovimientoController {

    private final MovimientoRepository movimientoRepository;
    private final MovimientoService movimientoService;

    public MovimientoController(
            MovimientoRepository movimientoRepository,
            MovimientoService movimientoService
    ) {
        this.movimientoRepository = movimientoRepository;
        this.movimientoService = movimientoService;
    }

    @GetMapping
    public List<Movimiento> listar() {
        return movimientoRepository.findAll(
                Sort.by(Sort.Direction.ASC, "id")
        );
    }

    @PostMapping
    public ResponseEntity<Movimiento> registrar(
            @Valid @RequestBody MovimientoRequest solicitud
    ) {
        Movimiento movimiento = movimientoService.registrar(solicitud);
        return ResponseEntity.status(HttpStatus.CREATED).body(movimiento);
    }
}