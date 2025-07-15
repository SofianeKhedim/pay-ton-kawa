package com.example.clientapi.entity;

/**
 * Énumération représentant le rôle d'un utilisateur.
 */
public enum UserRole {
    CLIENT("Client"),
    ADMIN("Administrateur");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}