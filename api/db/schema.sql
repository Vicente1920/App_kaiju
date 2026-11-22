CREATE TABLE IF NOT EXISTS productos (
    codigo        TEXT PRIMARY KEY,
    nombre        TEXT NOT NULL,
    categoria     TEXT NOT NULL,
    precio        INTEGER NOT NULL CHECK (precio >= 0),
    stock         INTEGER NOT NULL CHECK (stock >= 0),
    stock_minimo  INTEGER NOT NULL CHECK (stock_minimo >= 0),
    talla         TEXT,
    color         TEXT,
    modelo        TEXT
);

CREATE TABLE IF NOT EXISTS movimientos (
    id               SERIAL PRIMARY KEY,
    codigo_producto  TEXT NOT NULL REFERENCES productos(codigo) ON DELETE RESTRICT,
    tipo             TEXT NOT NULL CHECK (tipo IN ('Entrada', 'Salida', 'Ajuste')),
    cantidad         INTEGER NOT NULL CHECK (cantidad > 0),
    motivo           TEXT NOT NULL,
    fecha            TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
