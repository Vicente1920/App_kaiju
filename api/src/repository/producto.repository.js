import pool from "../config/db.js";

const COLUMNAS = `
    codigo,
    nombre,
    categoria,
    precio,
    stock,
    stock_minimo AS "stockMinimo",
    talla,
    color,
    modelo
`;

export async function listarProductos() {
    const resultado = await pool.query(
        `SELECT ${COLUMNAS} FROM productos ORDER BY codigo`
    );
    return resultado.rows;
}

// Si bloquear es true, la fila queda bloqueada (FOR UPDATE) hasta que termine la transacción.
export async function buscarProductoPorCodigo(codigo, { cliente = pool, bloquear = false } = {}) {
    const resultado = await cliente.query(
        `SELECT ${COLUMNAS} FROM productos WHERE codigo = $1 ${bloquear ? "FOR UPDATE" : ""}`,
        [codigo]
    );
    return resultado.rows[0];
}

export async function actualizarStock(codigo, nuevoStock, cliente = pool) {
    const resultado = await cliente.query(
        `UPDATE productos SET stock = $1 WHERE codigo = $2 RETURNING ${COLUMNAS}`,
        [nuevoStock, codigo]
    );
    return resultado.rows[0] ?? null;
}
export async function crearProducto(producto) {
    const {
        codigo,
        nombre,
        categoria,
        precio,
        stock,
        stockMinimo,
        talla = null,
        color = null,
        modelo = null
    } = producto;

    const resultado = await pool.query(
        `INSERT INTO productos (
            codigo,
            nombre,
            categoria,
            precio,
            stock,
            stock_minimo,
            talla,
            color,
            modelo
        )
        VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9)
        RETURNING ${COLUMNAS}`,
        [
            codigo,
            nombre,
            categoria,
            precio,
            stock,
            stockMinimo,
            talla,
            color,
            modelo
        ]
    );

    return resultado.rows[0];
}
