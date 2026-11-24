package com.kaiju.api;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MovimientoService {

    private final ProductoRepository productoRepository;
    private final MovimientoRepository movimientoRepository;

    public MovimientoService(
            ProductoRepository productoRepository,
            MovimientoRepository movimientoRepository
    ) {
        this.productoRepository = productoRepository;
        this.movimientoRepository = movimientoRepository;
    }

    @Transactional
    public Movimiento registrar(MovimientoRequest solicitud) {
        Producto producto = productoRepository
                .buscarPorCodigoConBloqueo(solicitud.getCodigoProducto())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Producto no encontrado"
                ));

        int nuevoStock = "Salida".equals(solicitud.getTipo())
                ? producto.getStock() - solicitud.getCantidad()
                : producto.getStock() + solicitud.getCantidad();

        if (nuevoStock < 0) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No hay stock suficiente"
            );
        }

        producto.setStock(nuevoStock);

        Movimiento movimiento = new Movimiento(
                solicitud.getCodigoProducto(),
                solicitud.getTipo(),
                solicitud.getCantidad(),
                solicitud.getMotivo()
        );

        return movimientoRepository.save(movimiento);
    }
}