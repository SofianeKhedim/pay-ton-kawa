package com.example.mspr4.Services;

import com.example.mspr4.Entities.Product;
import org.springframework.data.domain.Page;
import java.util.List;

public interface IProductService {
    Product createProduct(Product product);
    Product updateProduct(Product product, int id);
    void deleteProduct(int id);
    Product getProductById(int id);
    Page<Product> allProducts(int page, int size);
    List<Product> getAllProducts();
    double totalStockValue();
}