INSERT INTO productos (codigo, nombre, categoria, precio, stock, stock_minimo, talla, color, modelo) VALUES
  ('KJ-001', 'Polera Kaiju Negra M',    'Ropa',       12990, 24, 5, 'M',  'Negro', NULL),
  ('KJ-014', 'Zapatilla Urbana 42',     'Calzado',    39990,  3, 5, '42', 'Negro', 'Runner'),
  ('KJ-022', 'Mochila Táctica',         'Accesorios', 29990,  0, 4, NULL, NULL,    NULL),
  ('KJ-031', 'Gorro Lana Gris',         'Ropa',        8990, 18, 5, NULL, NULL,    NULL),
  ('KJ-040', 'Botella Térmica 750 ml',  'Hogar',      14990,  6, 8, NULL, NULL,    NULL)
ON CONFLICT (codigo) DO NOTHING;

