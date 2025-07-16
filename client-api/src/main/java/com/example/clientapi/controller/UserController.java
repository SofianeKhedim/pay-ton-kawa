package com.example.clientapi.controller;

import com.example.clientapi.dto.UserDto;
import com.example.clientapi.dto.CreateUserDto;
import com.example.clientapi.dto.UpdateUserDto;
import com.example.clientapi.entity.UserStatus;
import com.example.clientapi.entity.UserRole;
import com.example.clientapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Contrôleur REST pour la gestion des utilisateurs avec restrictions de rôles.
 */
@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "User Management", description = "API pour la gestion des utilisateurs PayeTonKawa")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Crée un nouvel utilisateur (admin uniquement).
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Créer un nouvel utilisateur", description = "Crée un nouvel utilisateur (réservé aux admins)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Utilisateur créé avec succès"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "409", description = "Email déjà existant"),
            @ApiResponse(responseCode = "403", description = "Accès refusé")
    })
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody CreateUserDto createUserDto) {
        logger.info("Requête de création d'un utilisateur reçue pour l'email: {} avec le rôle: {}",
                createUserDto.getEmail(), createUserDto.getRole());

        UserDto createdUser = userService.createUser(createUserDto);

        logger.info("Utilisateur créé avec succès. ID: {}", createdUser.getId());
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    /**
     * Récupère un utilisateur par son ID (le user lui-même ou admin).
     */
    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un utilisateur par ID")
    public ResponseEntity<UserDto> getUserById(@Parameter(description = "ID de l'utilisateur") @PathVariable Long id) {
        logger.debug("Requête de récupération de l'utilisateur avec l'ID: {}", id);

        UserDto user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Récupère un utilisateur par son email (le user lui-même ou admin).
     */
    @GetMapping("/email/{email}")
    @PreAuthorize("hasRole('ADMIN') or #email == authentication.name")
    @Operation(summary = "Récupérer un utilisateur par email")
    public ResponseEntity<UserDto> getUserByEmail(@Parameter(description = "Email de l'utilisateur") @PathVariable String email) {
        logger.debug("Requête de récupération de l'utilisateur avec l'email: {}", email);

        UserDto user = userService.getUserByEmail(email);
        return ResponseEntity.ok(user);
    }

    /**
     * Récupère tous les utilisateurs (admin uniquement).
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lister tous les utilisateurs")
    public ResponseEntity<Page<UserDto>> getAllUsers(@PageableDefault(size = 20, sort = "lastName") Pageable pageable) {
        logger.debug("Requête de récupération de tous les utilisateurs. Page: {}, Taille: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<UserDto> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    /**
     * Récupère uniquement les clients (admin uniquement).
     */
    @GetMapping("/clients")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lister tous les clients")
    public ResponseEntity<Page<UserDto>> getAllClients(@PageableDefault(size = 20, sort = "lastName") Pageable pageable) {
        logger.debug("Requête de récupération de tous les clients");

        Page<UserDto> clients = userService.getAllClients(pageable);
        return ResponseEntity.ok(clients);
    }

    /**
     * Récupère uniquement les administrateurs (admin uniquement).
     */
    @GetMapping("/admins")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lister tous les administrateurs")
    public ResponseEntity<Page<UserDto>> getAllAdmins(@PageableDefault(size = 20, sort = "lastName") Pageable pageable) {
        logger.debug("Requête de récupération de tous les administrateurs");

        Page<UserDto> admins = userService.getAllAdmins(pageable);
        return ResponseEntity.ok(admins);
    }

    /**
     * Met à jour un utilisateur (le user lui-même ou admin).
     */
    @PutMapping("/{id}")
    @Operation(summary = "Mettre à jour un utilisateur")
    public ResponseEntity<UserDto> updateUser(
            @Parameter(description = "ID de l'utilisateur") @PathVariable Long id,
            @Valid @RequestBody UpdateUserDto updateUserDto) {

        logger.info("Requête de mise à jour de l'utilisateur avec l'ID: {}", id);

        // Restriction : seuls les admins peuvent changer le rôle
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        if (updateUserDto.getRole() != null && !isAdmin) {
            updateUserDto.setRole(null); // Ignorer la modification de rôle si pas admin
            logger.warn("Tentative de modification de rôle par un non-admin refusée pour l'utilisateur ID: {}", id);
        }

        UserDto updatedUser = userService.updateUser(id, updateUserDto);

        logger.info("Utilisateur mis à jour avec succès. ID: {}", id);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Supprime un utilisateur (admin uniquement).
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Supprimer un utilisateur")
    public ResponseEntity<Void> deleteUser(@Parameter(description = "ID de l'utilisateur") @PathVariable Long id) {
        logger.info("Requête de suppression de l'utilisateur avec l'ID: {}", id);

        userService.deleteUser(id);

        logger.info("Utilisateur supprimé avec succès. ID: {}", id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Recherche les utilisateurs par statut (admin uniquement).
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lister les utilisateurs par statut")
    public ResponseEntity<Page<UserDto>> getUsersByStatus(
            @Parameter(description = "Statut de l'utilisateur") @PathVariable UserStatus status,
            @PageableDefault(size = 20, sort = "lastName") Pageable pageable) {

        logger.debug("Requête de récupération des utilisateurs avec le statut: {}", status);

        Page<UserDto> users = userService.getUsersByStatus(status, pageable);
        return ResponseEntity.ok(users);
    }

    /**
     * Recherche les utilisateurs par rôle (admin uniquement).
     */
    @GetMapping("/role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lister les utilisateurs par rôle")
    public ResponseEntity<Page<UserDto>> getUsersByRole(
            @Parameter(description = "Rôle de l'utilisateur") @PathVariable UserRole role,
            @PageableDefault(size = 20, sort = "lastName") Pageable pageable) {

        logger.debug("Requête de récupération des utilisateurs avec le rôle: {}", role);

        Page<UserDto> users = userService.getUsersByRole(role, pageable);
        return ResponseEntity.ok(users);
    }

    /**
     * Recherche globale dans les utilisateurs (admin uniquement).
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Rechercher des utilisateurs")
    public ResponseEntity<Page<UserDto>> searchUsers(
            @Parameter(description = "Terme de recherche") @RequestParam String q,
            @PageableDefault(size = 20, sort = "lastName") Pageable pageable) {

        logger.debug("Requête de recherche globale avec le terme: {}", q);

        Page<UserDto> users = userService.searchUsers(q, pageable);
        return ResponseEntity.ok(users);
    }

    /**
     * Active un utilisateur (admin uniquement).
     */
    @PatchMapping("/{id}/activate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activer un utilisateur")
    public ResponseEntity<UserDto> activateUser(@Parameter(description = "ID de l'utilisateur") @PathVariable Long id) {
        logger.info("Requête d'activation de l'utilisateur avec l'ID: {}", id);

        UserDto activatedUser = userService.activateUser(id);

        logger.info("Utilisateur activé avec succès. ID: {}", id);
        return ResponseEntity.ok(activatedUser);
    }

    /**
     * Désactive un utilisateur (admin uniquement).
     */
    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Désactiver un utilisateur")
    public ResponseEntity<UserDto> deactivateUser(@Parameter(description = "ID de l'utilisateur") @PathVariable Long id) {
        logger.info("Requête de désactivation de l'utilisateur avec l'ID: {}", id);

        UserDto deactivatedUser = userService.deactivateUser(id);

        logger.info("Utilisateur désactivé avec succès. ID: {}", id);
        return ResponseEntity.ok(deactivatedUser);
    }

    /**
     * Change le rôle d'un utilisateur (admin uniquement).
     */
    @PatchMapping("/{id}/role/{role}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Changer le rôle d'un utilisateur")
    public ResponseEntity<UserDto> changeUserRole(
            @Parameter(description = "ID de l'utilisateur") @PathVariable Long id,
            @Parameter(description = "Nouveau rôle") @PathVariable UserRole role) {

        logger.info("Requête de changement de rôle pour l'utilisateur avec l'ID: {} vers: {}", id, role);

        UserDto updatedUser = userService.changeUserRole(id, role);

        logger.info("Rôle changé avec succès pour l'utilisateur ID: {}", id);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * Récupère les statistiques des utilisateurs (admin uniquement).
     */
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Statistiques des utilisateurs")
    public ResponseEntity<Map<String, Object>> getUserStats() {
        logger.debug("Requête de récupération des statistiques des utilisateurs");

        Map<String, Object> stats = new HashMap<>();
        stats.put("total", userService.countUsers());
        stats.put("active", userService.countUsersByStatus(UserStatus.ACTIVE));
        stats.put("inactive", userService.countUsersByStatus(UserStatus.INACTIVE));
        stats.put("suspended", userService.countUsersByStatus(UserStatus.SUSPENDED));
        stats.put("pending", userService.countUsersByStatus(UserStatus.PENDING));
        stats.put("clients", userService.countUsersByRole(UserRole.CLIENT));
        stats.put("admins", userService.countUsersByRole(UserRole.ADMIN));

        return ResponseEntity.ok(stats);
    }

    /**
     * Profil de l'utilisateur connecté.
     */
    @GetMapping("/profile")
    @Operation(summary = "Profil de l'utilisateur connecté")
    public ResponseEntity<UserDto> getCurrentUserProfile(Authentication authentication) {
        logger.debug("Requête de récupération du profil pour l'utilisateur: {}", authentication.getName());

        UserDto user = userService.getUserByEmail(authentication.getName());
        return ResponseEntity.ok(user);
    }

    /**
     * Health check endpoint (public).
     */
    @GetMapping("/health")
    @Operation(summary = "Health check")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        logger.debug("Health check du service utilisateurs");

        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "user-api");
        health.put("timestamp", java.time.LocalDateTime.now());
        health.put("version", "1.0");

        return ResponseEntity.ok(health);
    }
}