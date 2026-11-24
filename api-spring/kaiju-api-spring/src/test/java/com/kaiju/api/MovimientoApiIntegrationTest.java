package com.kaiju.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
class MovimientoApiIntegrationTest {

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
    void entradaActualizaStockYGuardaMovimiento() throws Exception {
        guardarProducto("KJ-TEST", 5);

        mockMvc.perform(post("/api/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"
                                + "\"codigoProducto\":\"KJ-TEST\","
                                + "\"tipo\":\"Entrada\","
                                + "\"cantidad\":2,"
                                + "\"motivo\":\"Prueba automática\""
                                + "}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigoProducto").value("KJ-TEST"))
                .andExpect(jsonPath("$.tipo").value("Entrada"))
                .andExpect(jsonPath("$.fecha").exists());

        assertEquals(
                7,
                productoRepository.findById("KJ-TEST").orElseThrow().getStock()
        );
        assertEquals(1, movimientoRepository.count());
    }

    @Test
    void salidaMayorAlStockDevuelve409YSinCambiarDatos() throws Exception {
        guardarProducto("KJ-TEST", 1);

        mockMvc.perform(post("/api/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"
                                + "\"codigoProducto\":\"KJ-TEST\","
                                + "\"tipo\":\"Salida\","
                                + "\"cantidad\":2,"
                                + "\"motivo\":\"Prueba de stock insuficiente\""
                                + "}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensaje")
                        .value("No hay stock suficiente"));

        assertEquals(
                1,
                productoRepository.findById("KJ-TEST").orElseThrow().getStock()
        );
        assertEquals(0, movimientoRepository.count());
    }

    @Test
    void productoInexistenteDevuelve404() throws Exception {
        mockMvc.perform(post("/api/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"
                                + "\"codigoProducto\":\"NO-EXISTE\","
                                + "\"tipo\":\"Entrada\","
                                + "\"cantidad\":1,"
                                + "\"motivo\":\"Prueba automática\""
                                + "}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje")
                        .value("Producto no encontrado"));

        assertEquals(0, movimientoRepository.count());
    }

    @Test
    void listarMovimientosDevuelveCamposEsperadosPorAndroid()
            throws Exception {
        guardarProducto("KJ-TEST", 5);
        movimientoRepository.saveAndFlush(
                new Movimiento("KJ-TEST", "Entrada", 2, "Prueba")
        );

        mockMvc.perform(get("/api/movimientos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigoProducto").value("KJ-TEST"))
                .andExpect(jsonPath("$[0].tipo").value("Entrada"))
                .andExpect(jsonPath("$[0].cantidad").value(2))
                .andExpect(jsonPath("$[0].fecha").exists());
    }

    @Test
    void cantidadCeroDevuelve400YSinCambiarDatos() throws Exception {
        guardarProducto("KJ-TEST", 5);

        mockMvc.perform(post("/api/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{"
                                + "\"codigoProducto\":\"KJ-TEST\","
                                + "\"tipo\":\"Entrada\","
                                + "\"cantidad\":0,"
                                + "\"motivo\":\"Prueba inválida\""
                                + "}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje")
                        .value("La cantidad debe ser mayor que 0"));

        assertEquals(
                5,
                productoRepository.findById("KJ-TEST").orElseThrow().getStock()
        );
        assertEquals(0, movimientoRepository.count());
    }

    private void guardarProducto(String codigo, int stock) {
        Producto producto = new Producto();
        producto.setCodigo(codigo);
        producto.setNombre("Producto de prueba");
        producto.setCategoria("Pruebas");
        producto.setPrecio(1000);
        producto.setStock(stock);
        producto.setStockMinimo(1);

        productoRepository.saveAndFlush(producto);
    }
}