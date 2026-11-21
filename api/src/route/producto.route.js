import { Router } from "express";
import {
    listar,
    obtenerPorCodigo
} from "../controller/producto.controller.js";

const router = Router();

router.get("/", listar);
router.get("/:codigo", obtenerPorCodigo);

export default router;