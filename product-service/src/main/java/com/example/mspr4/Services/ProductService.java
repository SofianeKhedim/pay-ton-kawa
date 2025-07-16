package com.example.mspr4.Services;

import com.example.mspr4.Entities.Product;
import com.example.mspr4.Exception.ProductNotFoundException;
import com.example.mspr4.Repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j

public class ProductService implements IProductService {

    private final ProductRepository productRepository;
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    @Override
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(Product product, int id) {
        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produit non trouvé"));
        existing.setName(product.getName());
        existing.setDescription(product.getDescription());
        existing.setPrice(product.getPrice());
        existing.setQuantity(product.getQuantity());
        return productRepository.save(existing);
    }

    @Override

    public void deleteProduct(int id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        productRepository.delete(product);
        System.out.println("Produit ID=" + id + " a été bien supprimé.");
    }


    @Override
    public Product getProductById(int id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }


    @Override
    public Page<Product> allProducts(int page, int size) {
        return productRepository.findAll(PageRequest.of(page, size));
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public double totalStockValue() {
        return productRepository.findAll().stream()
                .mapToDouble(p -> p.getPrice() * p.getQuantity())
                .sum();
    }
}