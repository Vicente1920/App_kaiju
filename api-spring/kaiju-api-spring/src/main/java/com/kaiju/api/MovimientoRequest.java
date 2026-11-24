package com.kaiju.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public class MovimientoRequest {

    @NotBlank
    private String codigoProducto;

    @NotBlank
    @Pattern(
            regexp = "Entrada|Salida|Ajuste",
            message = "Tipo de movimiento inválido"
    )
    private String tipo;

    @NotNull
    @Positive(message = "La cantidad debe ser mayor que 0")
    private Integer cantidad;

    @NotBlank
    private String motivo;

    public MovimientoRequest() {
    }

    public String getCodigoProducto() {
        return codigoProducto;
    }

    public void setCodigoProducto(String codigoProducto) {
        this.codigoProducto = codigoProducto;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}