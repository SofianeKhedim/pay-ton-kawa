package com.example.mspr4.Controllers;

import com.example.mspr4.Entities.Product;
import com.example.mspr4.Services.IProductService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController

@RequestMapping("/api/product")
public class ProductController {


    private final IProductService productService;

    public ProductController(IProductService productService) {
        this.productService = productService;
    }

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
}