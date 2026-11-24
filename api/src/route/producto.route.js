import { Router } from "express";
import {
    listar,
    obtenerPorCodigo,
    crear
} from "../controller/producto.controller.js";

const router = Router();

router.get("/", listar);
router.post("/", crear);
router.get("/:codigo", obtenerPorCodigo);

export default router;