import {
    obtenerProductos,
    obtenerProductoPorCodigo
} from "../service/producto.service.js";

export function listar(req, res) {
    res.json(obtenerProductos());
}

export function obtenerPorCodigo(req, res) {
    const producto = obtenerProductoPorCodigo(req.params.codigo);

    if (!producto) {
        return res.status(404).json({
            mensaje: "Producto no encontrado"
        });
    }

    res.json(producto);
}