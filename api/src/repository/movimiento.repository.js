const movimientos = [];

export function listarMovimientos() {
    return movimientos;
}

export function guardarMovimiento(movimiento) {
    const nuevoMovimiento = {
        id: movimientos.length + 1,
        ...movimiento,
        fecha: new Date().toISOString()
    };

    movimientos.push(nuevoMovimiento);
    return nuevoMovimiento;
}