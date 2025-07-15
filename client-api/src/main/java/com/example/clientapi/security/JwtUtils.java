package com.example.clientapi.security;

import com.example.clientapi.service.TokenBlacklistService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * Utilitaire pour la gestion des tokens JWT.
 * 
 * Cette classe gère la création, validation et révocation des tokens JWT
 * utilisés pour l'authentification dans l'API.
 */
@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${app.jwt.secret:mySecretKey}")
    private String jwtSecret;

    @Value("${app.jwt.expirationMs:86400000}") // 24 heures par défaut
    private int jwtExpirationMs;

    @Autowired
    private TokenBlacklistService tokenBlacklistService;

    /**
     * Génère une clé secrète à partir de la chaîne de configuration.
     */
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    /**
     * Génère un token JWT à partir d'une authentification.
     */
    public String generateJwtToken(Authentication authentication) {
        UserDetails userPrincipal = (UserDetails) authentication.getPrincipal();
        return generateTokenFromUsername(userPrincipal.getUsername());
    }

    /**
     * Génère un token JWT à partir d'un nom d'utilisateur.
     */
    public String generateTokenFromUsername(String username) {
        Date issuedAt = new Date();
        Date expiryDate = new Date(issuedAt.getTime() + jwtExpirationMs);

        String token = Jwts.builder()
                .subject(username)
                .issuedAt(issuedAt)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();

        logger.debug("Token JWT généré pour l'utilisateur: {}", username);
        return token;
    }

    /**
     * Extrait le nom d'utilisateur du token JWT.
     */
    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    /**
     * Extrait la date d'expiration du token JWT.
     */
    public LocalDateTime getExpirationFromJwtToken(String token) {
        try {
            Date expiration = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload()
                    .getExpiration();
            
            return LocalDateTime.ofInstant(expiration.toInstant(), ZoneId.systemDefault());
        } catch (Exception e) {
            logger.error("Erreur lors de l'extraction de la date d'expiration du token", e);
            return LocalDateTime.now().plusDays(1); // Valeur par défaut
        }
    }

    /**
     * Valide un token JWT en vérifiant sa signature, son expiration et s'il n'est pas révoqué.
     */
    public boolean validateJwtToken(String authToken) {
        try {
            // Vérifier d'abord si le token est dans la blacklist
            if (tokenBlacklistService.isTokenRevoked(authToken)) {
                logger.warn("Token révoqué détecté dans la blacklist");
                return false;
            }

            // Valider la signature et l'expiration
            Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(authToken);
            
            return true;
        } catch (MalformedJwtException e) {
            logger.error("Token JWT malformé: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("Token JWT expiré: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("Token JWT non supporté: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string vide: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Erreur de validation du token JWT", e);
        }
        return false;
    }

    /**
     * Révoque un token en l'ajoutant à la blacklist.
     */
    public void revokeToken(String token) {
        try {
            LocalDateTime expiration = getExpirationFromJwtToken(token);
            tokenBlacklistService.revokeToken(token, expiration);
            logger.info("Token révoqué avec succès");
        } catch (Exception e) {
            logger.error("Erreur lors de la révocation du token", e);
            // Même en cas d'erreur, on ajoute le token à la blacklist avec une expiration par défaut
            tokenBlacklistService.revokeToken(token, LocalDateTime.now().plusDays(1));
        }
    }

    /**
     * Vérifie si un token est expiré.
     */
    public boolean isTokenExpired(String token) {
        try {
            LocalDateTime expiration = getExpirationFromJwtToken(token);
            return expiration.isBefore(LocalDateTime.now());
        } catch (Exception e) {
            logger.error("Erreur lors de la vérification de l'expiration du token", e);
            return true; // En cas d'erreur, on considère le token comme expiré
        }
    }
}