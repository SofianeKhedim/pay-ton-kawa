package com.example.clientapi.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO pour l'inscription utilisateur.
 */
public class RegisterRequest {

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(max = 50, message = "Le prénom ne peut pas dépasser 50 caractères")
    private String firstName;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 50, message = "Le nom ne peut pas dépasser 50 caractères")
    private String lastName;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit être valide")
    @Size(max = 100, message = "L'email ne peut pas dépasser 100 caractères")
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 6, max = 100, message = "Le mot de passe doit contenir entre 6 et 100 caractères")
    private String password;

    @Size(max = 20, message = "Le téléphone ne peut pas dépasser 20 caractères")
    private String phone;

    @Size(max = 200, message = "L'adresse ne peut pas dépasser 200 caractères")
    private String address;

    @Size(max = 50, message = "La ville ne peut pas dépasser 50 caractères")
    private String city;

    @Size(max = 10, message = "Le code postal ne peut pas dépasser 10 caractères")
    private String postalCode;

    @Size(max = 50, message = "Le pays ne peut pas dépasser 50 caractères")
    private String country;

    @Size(max = 100, message = "Le nom de l'entreprise ne peut pas dépasser 100 caractères")
    private String companyName;

    // Le rôle n'est pas dans le DTO d'inscription pour les clients
    // Il sera défini automatiquement à CLIENT

    // Constructeurs
    public RegisterRequest() {}

    // Getters et Setters
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
}