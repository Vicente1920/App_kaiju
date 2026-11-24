package com.kaiju.api;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/api/health")
    public Map<String, Object> salud() {
        return Map.of(
                "ok", true,
                "servicio", "Kaiju API",
                "mensaje", "El servidor está funcionando"
        );
    }
}