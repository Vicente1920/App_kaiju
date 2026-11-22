import {
    obtenerMovimientos,
    registrarMovimiento
} from "../service/movimiento.service.js";

export async function listar(req, res) {
    const movimientos = await obtenerMovimientos();
    res.json(movimientos);
}

export async function registrar(req, res) {
    const resultado = await registrarMovimiento(req.body);

    if (resultado.error) {
        return res.status(resultado.codigo).json({
            mensaje: resultado.error
        });
    }

    res.status(201).json(resultado.movimiento);
}