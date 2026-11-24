import {
    listarProductos,
    buscarProductoPorCodigo,
    crearProducto as insertarProducto
} from "../repository/producto.repository.js";

export async function obtenerProductos() {
    return await listarProductos();
}

export async function obtenerProductoPorCodigo(codigo) {
    return await buscarProductoPorCodigo(codigo);
}

export async function registrarProducto(datos) {
    const {
        codigo,
        nombre,
        categoria,
        precio,
        stock,
        stockMinimo,
        talla,
        color,
        modelo
    } = datos ?? {};

    const camposTextoValidos =
        typeof codigo === "string" && codigo.trim() !== "" &&
        typeof nombre === "string" && nombre.trim() !== "" &&
        typeof categoria === "string" && categoria.trim() !== "";

    const numerosValidos =
        Number.isInteger(precio) &&
        Number.isInteger(stock) &&
        Number.isInteger(stockMinimo);

    if (!camposTextoValidos || !numerosValidos) {
        return {
            error: "Código, nombre, categoría, precio, stock y stock mínimo son obligatorios y válidos",
            codigo: 400
        };
    }

    if (precio < 0 || stock < 0 || stockMinimo < 0) {
        return {
            error: "Precio, stock y stock mínimo no pueden ser negativos",
            codigo: 400
        };
    }

    try {
        const producto = await insertarProducto({
            codigo: codigo.trim(),
            nombre: nombre.trim(),
            categoria: categoria.trim(),
            precio,
            stock,
            stockMinimo,
            talla: typeof talla === "string" && talla.trim() !== "" ? talla.trim() : null,
            color: typeof color === "string" && color.trim() !== "" ? color.trim() : null,
            modelo: typeof modelo === "string" && modelo.trim() !== "" ? modelo.trim() : null
        });

        return { producto };
    } catch (error) {
        if (error.code === "23505") {
            return {
                error: "Ya existe un producto con ese código",
                codigo: 409
            };
        }

        throw error;
    }
}