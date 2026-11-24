package com.kaiju.api;

import java.time.Instant;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "movimientos")
public class Movimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "codigo_producto", nullable = false)
    private String codigoProducto;

    @Column(name = "tipo", nullable = false)
    private String tipo;

    @Column(name = "cantidad", nullable = false)
    private Integer cantidad;

    @Column(name = "motivo", nullable = false)
    private String motivo;

    @CreationTimestamp
    @Column(name = "fecha", nullable = false, updatable = false)
    private Instant fecha;

    public Movimiento() {
    }

    public Movimiento(String codigoProducto, String tipo, Integer cantidad, String motivo) {
        this.codigoProducto = codigoProducto;
        this.tipo = tipo;
        this.cantidad = cantidad;
        this.motivo = motivo;
    }

    public Integer getId() {
        return id;
    }

    public String getCodigoProducto() {
        return codigoProducto;
    }

    public String getTipo() {
        return tipo;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public String getMotivo() {
        return motivo;
    }

    public Instant getFecha() {
        return fecha;
    }
}