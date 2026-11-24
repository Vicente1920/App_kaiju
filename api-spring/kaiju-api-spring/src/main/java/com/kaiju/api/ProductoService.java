package com.kaiju.api;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ProductoService {

    private static final String MENSAJE_DUPLICADO =
            "Ya existe un producto con ese código";

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Transactional
    public Producto crear(ProductoRequest solicitud) {
        if (productoRepository.existsById(solicitud.getCodigo())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    MENSAJE_DUPLICADO
            );
        }

        Producto producto = new Producto();
        producto.setCodigo(solicitud.getCodigo());
        producto.setNombre(solicitud.getNombre());
        producto.setCategoria(solicitud.getCategoria());
        producto.setPrecio(solicitud.getPrecio());
        producto.setStock(solicitud.getStock());
        producto.setStockMinimo(solicitud.getStockMinimo());
        producto.setTalla(solicitud.getTalla());
        producto.setColor(solicitud.getColor());
        producto.setModelo(solicitud.getModelo());

        try {
            return productoRepository.saveAndFlush(producto);
        } catch (DataIntegrityViolationException error) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    MENSAJE_DUPLICADO
            );
        }
    }
}