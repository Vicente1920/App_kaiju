import {
    obtenerProductos,
    obtenerProductoPorCodigo
} from "../service/producto.service.js";

export async function listar(req, res) {
    const productos = await obtenerProductos();
    res.json(productos);
}

export async function obtenerPorCodigo(req, res) {
    const producto = await obtenerProductoPorCodigo(req.params.codigo);

    if (!producto) {
        return res.status(404).json({
            mensaje: "Producto no encontrado"
        });
    }

    res.json(producto);
}