import pool from "../config/db.js";

const COLUMNAS = `
    id,
    codigo_producto AS "codigoProducto",
    tipo,
    cantidad,
    motivo,
    fecha
`;

export async function listarMovimientos() {
    const resultado = await pool.query(
        `SELECT ${COLUMNAS} FROM movimientos ORDER BY id`
    );
    return resultado.rows;
}

// cliente: la conexión de la transacción (si no se pasa, usa el pool).
export async function guardarMovimiento({ codigoProducto, tipo, cantidad, motivo }, cliente = pool) {
    const resultado = await cliente.query(
        `INSERT INTO movimientos (codigo_producto, tipo, cantidad, motivo)
         VALUES ($1, $2, $3, $4)
         RETURNING ${COLUMNAS}`,
        [codigoProducto, tipo, cantidad, motivo]
    );
    return resultado.rows[0];
}
