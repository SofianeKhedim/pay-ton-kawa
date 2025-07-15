package com.example.clientapi.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Service pour gérer la liste noire des tokens JWT révoqués.
 * 
 * Cette classe maintient une liste des tokens qui ont été explicitement
 * révoqués via la déconnexion et s'assure qu'ils ne peuvent plus être utilisés.
 */
@Service
public class TokenBlacklistService {

    private static final Logger logger = LoggerFactory.getLogger(TokenBlacklistService.class);

    // Map thread-safe pour stocker les tokens révoqués avec leur date d'expiration
    private final ConcurrentHashMap<String, LocalDateTime> blacklistedTokens = new ConcurrentHashMap<>();

    // Service pour nettoyer automatiquement les tokens expirés
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public TokenBlacklistService() {
        // Nettoyage automatique des tokens expirés toutes les heures
        scheduler.scheduleAtFixedRate(this::cleanupExpiredTokens, 1, 1, TimeUnit.HOURS);
        logger.info("Service de blacklist des tokens initialisé avec nettoyage automatique");
    }

    /**
     * Ajoute un token à la liste noire.
     * 
     * @param token Le token JWT à révoquer
     * @param expirationTime La date d'expiration du token
     */
    public void revokeToken(String token, LocalDateTime expirationTime) {
        blacklistedTokens.put(token, expirationTime);
        logger.info("Token révoqué et ajouté à la blacklist. Expiration: {}", expirationTime);
    }

    /**
     * Vérifie si un token est dans la liste noire.
     * 
     * @param token Le token à vérifier
     * @return true si le token est révoqué, false sinon
     */
    public boolean isTokenRevoked(String token) {
        boolean isRevoked = blacklistedTokens.containsKey(token);
        if (isRevoked) {
            logger.debug("Token trouvé dans la blacklist");
        }
        return isRevoked;
    }

    /**
     * Nettoie les tokens expirés de la liste noire.
     * Les tokens expirés n'ont plus besoin d'être stockés.
     */
    private void cleanupExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        int initialSize = blacklistedTokens.size();
        
        blacklistedTokens.entrySet().removeIf(entry -> 
            entry.getValue().isBefore(now)
        );
        
        int removedTokens = initialSize - blacklistedTokens.size();
        if (removedTokens > 0) {
            logger.info("Nettoyage de la blacklist: {} tokens expirés supprimés", removedTokens);
        }
    }

    /**
     * Retourne le nombre de tokens actuellement dans la liste noire.
     * 
     * @return Le nombre de tokens révoqués
     */
    public int getBlacklistedTokensCount() {
        return blacklistedTokens.size();
    }

    /**
     * Vide complètement la liste noire (utile pour les tests).
     */
    public void clearBlacklist() {
        blacklistedTokens.clear();
        logger.info("Blacklist des tokens vidée");
    }
}