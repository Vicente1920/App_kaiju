import {listarProductos, buscarProductoPorCodigo} from "../repository/producto.repository.js";

export async function obtenerProductos() {
    return await listarProductos();
}

export async function obtenerProductoPorCodigo(codigo) {
    return await buscarProductoPorCodigo(codigo);
}