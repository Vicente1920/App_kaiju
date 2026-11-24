package com.kaiju.api;

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
                        .content(jsonProducto()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.codigo").value("KJ-TEST"))
                .andExpect(jsonPath("$.stock").value(5));
    }

    @Test
    void codigoDuplicadoDevuelve409() throws Exception {
        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonProducto()))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/productos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonProducto()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensaje")
                        .value("Ya existe un producto con ese código"));
    }

    @Test
    void listarDevuelveCamposEsperadosPorAndroid() throws Exception {
        Producto producto = new Producto();
        producto.setCodigo("KJ-TEST");
        producto.setNombre("Pantalon de prueba");
        producto.setCategoria("Ropa");
        producto.setPrecio(12990);
        producto.setStock(5);
        producto.setStockMinimo(1);
        producto.setTalla("M");
        productoRepository.saveAndFlush(producto);

        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigo").value("KJ-TEST"))
                .andExpect(jsonPath("$[0].stockMinimo").value(1))
                .andExpect(jsonPath("$[0].talla").value("M"));
    }

    private String jsonProducto() {
        return "{"
                + "\"codigo\":\"KJ-TEST\","
                + "\"nombre\":\"Producto de prueba\","
                + "\"categoria\":\"Pruebas\","
                + "\"precio\":1000,"
                + "\"stock\":5,"
                + "\"stockMinimo\":1,"
                + "\"talla\":\"M\""
                + "}";
    }
}