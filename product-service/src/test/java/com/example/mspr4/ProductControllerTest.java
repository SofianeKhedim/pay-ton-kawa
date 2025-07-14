package com.example.mspr4;

import com.example.mspr4.Controllers.ProductController;
import com.example.mspr4.Entities.Product;
import com.example.mspr4.Services.IProductService;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


public class ProductControllerTest {

    private MockMvc mockMvc;
    private IProductService productService;

    @BeforeEach
    public void setup() {
        productService = mock(IProductService.class);
        ProductController productController = new ProductController(productService);
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
    }

    @Test
    public void testCreateProduct() throws Exception {
        Product product = new Product();
        product.setName("Produit Test");
        product.setDescription("Description test");
        product.setPrice(9.99);
        product.setQuantity(10);

        when(productService.createProduct(any(Product.class))).thenReturn(product);

        mockMvc.perform(post("/api/product/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(product)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Produit Test"));
    }

    @Test
    public void testGetProductById() throws Exception {
        Product product = new Product();
        product.setId(1);
        product.setName("Produit A");

        when(productService.getProductById(1)).thenReturn(product);

        mockMvc.perform(get("/api/product/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Produit A"));
    }

    @Test
    public void testGetAllProducts() throws Exception {
        Product p1 = new Product();
        Product p2 = new Product();
        when(productService.getAllProducts()).thenReturn(Arrays.asList(p1, p2));

        mockMvc.perform(get("/api/product/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    public void testGetStockTotal() throws Exception {
        when(productService.totalStockValue()).thenReturn(99.99);

        mockMvc.perform(get("/api/product/stock/total"))
                .andExpect(status().isOk())
                .andExpect(content().string("99.99"));
    }
}
