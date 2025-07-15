package com.example.clientapi.controller;

import com.example.clientapi.dto.auth.AdminRegisterRequest;
import com.example.clientapi.dto.auth.AuthResponse;
import com.example.clientapi.dto.auth.LoginRequest;
import com.example.clientapi.dto.auth.RegisterRequest;
import com.example.clientapi.security.JwtUtils;
import com.example.clientapi.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Contrôleur pour la gestion de l'authentification.
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "API pour l'authentification et l'inscription")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * Connexion utilisateur.
     */
    @PostMapping("/login")
    @Operation(summary = "Connexion utilisateur", description = "Authentifie un utilisateur et retourne un token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Connexion réussie"),
            @ApiResponse(responseCode = "401", description = "Identifiants invalides"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public ResponseEntity<AuthResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        logger.info("Requête de connexion reçue pour l'email: {}", loginRequest.getEmail());

        AuthResponse authResponse = authService.authenticateUser(loginRequest);

        logger.info("Connexion réussie pour l'utilisateur: {}", loginRequest.getEmail());
        return ResponseEntity.ok(authResponse);
    }

    /**
     * Inscription client (publique).
     */
    @PostMapping("/register")
    @Operation(summary = "Inscription client", description = "Inscrit un nouveau client dans le système")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inscription réussie"),
            @ApiResponse(responseCode = "409", description = "Email déjà existant"),
            @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    public ResponseEntity<AuthResponse> registerClient(@Valid @RequestBody RegisterRequest registerRequest) {
        logger.info("Requête d'inscription client reçue pour l'email: {}", registerRequest.getEmail());

        AuthResponse authResponse = authService.registerClient(registerRequest);

        logger.info("Inscription client réussie pour l'email: {}", registerRequest.getEmail());
        return ResponseEntity.ok(authResponse);
    }

    /**
     * Inscription administrateur (réservée aux admins).
     */
    @PostMapping("/register/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Inscription administrateur",
            description = "Inscrit un nouvel administrateur (réservé aux administrateurs existants)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inscription admin réussie"),
            @ApiResponse(responseCode = "409", description = "Email déjà existant"),
            @ApiResponse(responseCode = "400", description = "Données invalides"),
            @ApiResponse(responseCode = "403", description = "Accès refusé - Rôle admin requis")
    })
    public ResponseEntity<AuthResponse> registerAdmin(@Valid @RequestBody AdminRegisterRequest registerRequest) {
        logger.info("Requête d'inscription admin reçue pour l'email: {}", registerRequest.getEmail());

        AuthResponse authResponse = authService.registerAdmin(registerRequest);

        logger.info("Inscription admin réussie pour l'email: {}", registerRequest.getEmail());
        return ResponseEntity.ok(authResponse);
    }

    /**
     * Déconnexion utilisateur.
     */
    @PostMapping("/logout")
    @Operation(summary = "Déconnexion utilisateur", 
            description = "Déconnecte un utilisateur en révoquant son token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Déconnexion réussie"),
            @ApiResponse(responseCode = "400", description = "Token manquant ou invalide"),
            @ApiResponse(responseCode = "401", description = "Token non autorisé")
    })
    public ResponseEntity<Map<String, String>> logoutUser(HttpServletRequest request) {
        String token = parseJwtFromRequest(request);
        
        if (token == null) {
            logger.warn("Tentative de déconnexion sans token JWT");
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Token JWT manquant");
            errorResponse.put("message", "Un token d'authentification valide est requis pour la déconnexion");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        try {
            // Vérifier que le token est valide avant de le révoquer
            if (!jwtUtils.validateJwtToken(token)) {
                logger.warn("Tentative de déconnexion avec un token invalide");
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Token invalide");
                errorResponse.put("message", "Le token fourni n'est pas valide");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            // Extraire le nom d'utilisateur pour le log
            String username = jwtUtils.getUserNameFromJwtToken(token);
            
            // Révoquer le token
            jwtUtils.revokeToken(token);
            
            logger.info("Déconnexion réussie pour l'utilisateur: {}", username);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Déconnexion réussie");
            response.put("status", "success");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Erreur lors de la déconnexion", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Erreur de déconnexion");
            errorResponse.put("message", "Une erreur s'est produite lors de la déconnexion");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Vérification de la disponibilité d'un email.
     */
    @GetMapping("/check-email/{email}")
    @Operation(summary = "Vérifier la disponibilité d'un email",
            description = "Vérifie si un email est déjà utilisé")
    public ResponseEntity<Map<String, Boolean>> checkEmailAvailability(@PathVariable String email) {
        logger.debug("Vérification de la disponibilité de l'email: {}", email);

        boolean exists = authService.emailExists(email);
        Map<String, Boolean> response = new HashMap<>();
        response.put("available", !exists);
        response.put("exists", exists);

        return ResponseEntity.ok(response);
    }

    /**
     * Vérification du statut du token.
     */
    @GetMapping("/verify-token")
    @Operation(summary = "Vérifier la validité d'un token",
            description = "Vérifie si le token JWT actuel est valide et non révoqué")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token valide"),
            @ApiResponse(responseCode = "401", description = "Token invalide ou révoqué")
    })
    public ResponseEntity<Map<String, Object>> verifyToken(HttpServletRequest request) {
        String token = parseJwtFromRequest(request);
        
        Map<String, Object> response = new HashMap<>();
        
        if (token == null) {
            response.put("valid", false);
            response.put("message", "Token manquant");
            return ResponseEntity.ok(response);
        }

        try {
            boolean isValid = jwtUtils.validateJwtToken(token);
            
            if (isValid) {
                String username = jwtUtils.getUserNameFromJwtToken(token);
                response.put("valid", true);
                response.put("username", username);
                response.put("message", "Token valide");
            } else {
                response.put("valid", false);
                response.put("message", "Token invalide ou révoqué");
            }
            
        } catch (Exception e) {
            logger.error("Erreur lors de la vérification du token", e);
            response.put("valid", false);
            response.put("message", "Erreur de validation");
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * Extrait le token JWT de la requête HTTP.
     */
    private String parseJwtFromRequest(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        
        return null;
    }
}