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

export function listarProductos() {
    return productos;
}

export function buscarProductoPorCodigo(codigo) {
    return productos.find(producto => producto.codigo === codigo);
}