import express from "express";
import productoRoute from "./route/producto.route.js";
import movimientoRoute from "./route/movimiento.route.js";

const app = express();
const PORT = 3000;

app.use(express.json());

app.get("/api/health", (req, res) => {
    res.json({
        ok: true,
        servicio: "Kaiju API",
        mensaje: "El servidor está funcionando"
    });
});

app.use("/api/productos", productoRoute);
app.use("/api/movimientos", movimientoRoute);

app.listen(PORT, "0.0.0.0", () => {
    console.log(`Kaiju API funcionando en el puerto ${PORT}`);
});