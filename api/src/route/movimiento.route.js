import { Router } from "express";

import {
    listar,
    registrar
} from "../controller/movimiento.controller.js";

const router = Router();

router.get("/", listar);
router.post("/", registrar);

export default router;