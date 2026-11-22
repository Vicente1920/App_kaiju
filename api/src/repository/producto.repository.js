const productos = [
    {
        codigo: "P001",
        nombre: "Polera Kaiju",
        categoria: "Ropa",
        precio: 12990,
        stock: 10,
        stockMinimo: 2,
        talla: "M",
        color: "Negro",
        modelo: "Clásico"
    }
];

export async function listarProductos() {
    return productos;
}

export async function buscarProductoPorCodigo(codigo) {
    return productos.find(producto => producto.codigo === codigo);
}

export async function actualizarStock(codigo, nuevoStock) {
    const producto = productos.find(item => item.codigo === codigo);

    if (!producto) {
        return null;
    }

    producto.stock = nuevoStock;
    return producto;
}
