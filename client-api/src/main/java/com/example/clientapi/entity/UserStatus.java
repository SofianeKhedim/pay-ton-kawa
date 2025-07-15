package com.example.clientapi.entity;

/**
 * Énumération représentant le statut d'un utilisateur.
 */
public enum UserStatus {
    ACTIVE("Actif"),
    INACTIVE("Inactif"),
    SUSPENDED("Suspendu"),
    PENDING("En attente");

    private final String displayName;

    UserStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}