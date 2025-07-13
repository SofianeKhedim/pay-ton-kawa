package com.example.mspr4.Exception;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(int id) {
        super("Produit avec l'ID = " + id + " n'existe pas !");
    }
}