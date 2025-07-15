package com.example.clientapi.service;

import com.example.clientapi.dto.UserDto;
import com.example.clientapi.dto.CreateUserDto;
import com.example.clientapi.dto.UpdateUserDto;
import com.example.clientapi.entity.UserStatus;
import com.example.clientapi.entity.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Interface du service de gestion des utilisateurs avec sécurité.
 */
public interface UserService {

    /**
     * Crée un nouvel utilisateur.
     */
    UserDto createUser(CreateUserDto createUserDto);

    /**
     * Récupère un utilisateur par son ID.
     */
    UserDto getUserById(Long id);

    /**
     * Récupère un utilisateur par son email.
     */
    UserDto getUserByEmail(String email);

    /**
     * Récupère tous les utilisateurs avec pagination.
     */
    Page<UserDto> getAllUsers(Pageable pageable);

    /**
     * Récupère uniquement les clients.
     */
    Page<UserDto> getAllClients(Pageable pageable);

    /**
     * Récupère uniquement les administrateurs.
     */
    Page<UserDto> getAllAdmins(Pageable pageable);

    /**
     * Met à jour un utilisateur existant.
     */
    UserDto updateUser(Long id, UpdateUserDto updateUserDto);

    /**
     * Supprime un utilisateur.
     */
    void deleteUser(Long id);

    /**
     * Recherche les utilisateurs par statut.
     */
    Page<UserDto> getUsersByStatus(UserStatus status, Pageable pageable);

    /**
     * Recherche les utilisateurs par rôle.
     */
    Page<UserDto> getUsersByRole(UserRole role, Pageable pageable);

    /**
     * Recherche globale dans les utilisateurs.
     */
    Page<UserDto> searchUsers(String searchTerm, Pageable pageable);

    /**
     * Active un utilisateur.
     */
    UserDto activateUser(Long id);

    /**
     * Désactive un utilisateur.
     */
    UserDto deactivateUser(Long id);

    /**
     * Change le rôle d'un utilisateur.
     */
    UserDto changeUserRole(Long id, UserRole newRole);

    /**
     * Vérifie si un email existe déjà.
     */
    boolean emailExists(String email);

    /**
     * Compte le nombre total d'utilisateurs.
     */
    long countUsers();

    /**
     * Compte le nombre d'utilisateurs par statut.
     */
    long countUsersByStatus(UserStatus status);

    /**
     * Compte le nombre d'utilisateurs par rôle.
     */
    long countUsersByRole(UserRole role);

    /**
     * Vérifie si l'utilisateur connecté est propriétaire de l'ID donné.
     */
    boolean isOwner(Long userId, String authenticatedEmail);
}