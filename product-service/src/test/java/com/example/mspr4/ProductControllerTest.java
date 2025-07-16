package com.example.mspr4;

import com.example.mspr4.Controllers.ProductController;
import com.example.mspr4.Entities.Product;
import com.example.mspr4.Services.IProductService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ProductControllerTest {

    private MockMvc mockMvc;
    
    @Mock
    private IProductService productService;

    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        ProductController productController = new ProductController(productService);
        mockMvc = MockMvcBuilders.standaloneSetup(productController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    public void testCreateProduct() throws Exception {
        // Given
        Product inputProduct = new Product();
        inputProduct.setName("Produit Test");
        inputProduct.setDescription("Description test");
        inputProduct.setPrice(9.99);
        inputProduct.setQuantity(10);

        Product savedProduct = new Product();
        savedProduct.setId(1);
        savedProduct.setName("Produit Test");
        savedProduct.setDescription("Description test");
        savedProduct.setPrice(9.99);
        savedProduct.setQuantity(10);

        when(productService.createProduct(any(Product.class))).thenReturn(savedProduct);

        // When & Then
        mockMvc.perform(post("/api/product/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Produit Test"))
                .andExpect(jsonPath("$.description").value("Description test"))
                .andExpect(jsonPath("$.price").value(9.99))
                .andExpect(jsonPath("$.quantity").value(10));

        verify(productService, times(1)).createProduct(any(Product.class));
    }

    @Test
    public void testGetProductById() throws Exception {
        // Given
        Product product = new Product();
        product.setId(1);
        product.setName("Produit A");
        product.setDescription("Description A");
        product.setPrice(15.50);

        when(productService.getProductById(1)).thenReturn(product);

        // When & Then
        mockMvc.perform(get("/api/product/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Produit A"))
                .andExpect(jsonPath("$.description").value("Description A"))
                .andExpect(jsonPath("$.price").value(15.50));

        verify(productService, times(1)).getProductById(1);
    }

    @Test
    public void testGetAllProducts() throws Exception {
        // Given
        Product p1 = new Product();
        p1.setId(1);
        p1.setName("Produit 1");
        p1.setPrice(10.0);

        Product p2 = new Product();
        p2.setId(2);
        p2.setName("Produit 2");
        p2.setPrice(20.0);

        List<Product> products = Arrays.asList(p1, p2);
        when(productService.getAllProducts()).thenReturn(products);

        // When & Then
        mockMvc.perform(get("/api/product/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Produit 1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("Produit 2"));

        verify(productService, times(1)).getAllProducts();
    }

    @Test
    public void testUpdateProduct() throws Exception {
        // Given
        Product inputProduct = new Product();
        inputProduct.setName("Produit Modifié");
        inputProduct.setPrice(25.99);

        Product updatedProduct = new Product();
        updatedProduct.setId(1);
        updatedProduct.setName("Produit Modifié");
        updatedProduct.setPrice(25.99);

        when(productService.updateProduct(any(Product.class), anyInt())).thenReturn(updatedProduct);

        // When & Then
        mockMvc.perform(put("/api/product/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Produit Modifié"))
                .andExpect(jsonPath("$.price").value(25.99));

        verify(productService, times(1)).updateProduct(any(Product.class), eq(1));
    }

    @Test
    public void testDeleteProduct() throws Exception {
        // Given
        doNothing().when(productService).deleteProduct(1);

        // When & Then
        mockMvc.perform(delete("/api/product/delete/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("✅ Produit avec ID = 1 a été supprimé avec succès."));

        verify(productService, times(1)).deleteProduct(1);
    }

    @Test
    public void testGetStockTotal() throws Exception {
        // Given
        when(productService.totalStockValue()).thenReturn(99.99);

        // When & Then
        mockMvc.perform(get("/api/product/stock/total"))
                .andExpect(status().isOk())
                .andExpect(content().string("99.99"));

        verify(productService, times(1)).totalStockValue();
    }

    @Test
    public void testGetProductById_NotFound() throws Exception {
        // Given
        when(productService.getProductById(999)).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/api/product/999"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(productService, times(1)).getProductById(999);
    }
}