package com.example.mspr4.Controllers;

import com.example.mspr4.Entities.Product;
import com.example.mspr4.Services.IProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    private final IProductService productService;

    public ProductController(IProductService productService) {
        this.productService = productService;
    }

    // LECTURE - Accessible à tous les utilisateurs authentifiés
    @GetMapping("/{id}")
    public Product getProductById(@PathVariable("id") int id) {
        return productService.getProductById(id);
    }

    @GetMapping("/all")
    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @GetMapping("/stock/total")
    public double getTotalStockValue() {
        return productService.totalStockValue();
    }

    // ÉCRITURE - Admin uniquement (optionnel, pour l'instant tous les authentifiés)
    @PostMapping("/create")
    public Product createProduct(@RequestBody Product product) {
        return productService.createProduct(product);
    }

    @PutMapping("/update/{id}")
    public Product updateProduct(@RequestBody Product product, @PathVariable("id") int id) {
        return productService.updateProduct(product, id);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable("id") int id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("✅ Produit avec ID = " + id + " a été supprimé avec succès.");
    }

    // Health check - Public
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = Map.of(
            "status", "UP",
            "service", "product-api",
            "timestamp", java.time.LocalDateTime.now()
        );
        return ResponseEntity.ok(health);
    }
}