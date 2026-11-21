import {
    listarProductos,
    buscarProductoPorCodigo
} from "../repository/producto.repository.js";

export function obtenerProductos() {
    return listarProductos();
}

export function obtenerProductoPorCodigo(codigo) {
    return buscarProductoPorCodigo(codigo);
}