package com.example.mspr4.Entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String description;
    private Double price;
    private Integer quantity;
    // ----- Getters -----
    public int getId() {
        return id;
    }

    public String getNom() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Double getPrix() {
        return price;
    }

    public Integer getQuantite() {
        return quantity;
    }

    // ----- Setters -----
    public void setId(int id) {
        this.id = id;
    }

    public void setNom(String nom) {
        this.name = nom;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrix(Double prix) {
        this.price = prix;
    }

    public void setQuantite(Integer quantite) {
        this.quantity = quantite;
    }
}