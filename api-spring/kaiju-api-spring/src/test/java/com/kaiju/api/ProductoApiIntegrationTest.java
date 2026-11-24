package com.kaiju.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class ProductoApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private MovimientoRepository movimientoRepository;

    @BeforeEach
    void limpiarDatosDePrueba() {
        movimientoRepository.deleteAll();
        productoRepository.deleteAll();
    }

    @Test
    void creaProductoYDevuelve201() throws Exception {
        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"
                                + "\"codigo\":\"KJ-TEST\","
                                + "\"nombre\":\"Producto de prueba\","
                                + "\"categoria\":\"Pruebas\","
                                + "\"precio\":1000,"
                                + "\"stock\":5,"
                                + "\"stockMinimo\":1,"
                                + "\"talla\":\"M\""
                                + "}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigo").value("KJ-TEST"))
                .andExpect(jsonPath("$.stock").value(5));
    }

    @Test
    void codigoDuplicadoDevuelve409() throws Exception {
        String producto = "{"
                + "\"codigo\":\"KJ-TEST\","
                + "\"nombre\":\"Producto de prueba\","
                + "\"categoria\":\"Pruebas\","
                + "\"precio\":1000,"
                + "\"stock\":5,"
                + "\"stockMinimo\":1"
                + "}";

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(producto))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(producto))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensaje")
                        .value("Ya existe un producto con ese código"));
    }
}