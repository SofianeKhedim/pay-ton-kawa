package com.example.clientapi.repository;

import com.example.clientapi.entity.User;
import com.example.clientapi.entity.UserStatus;
import com.example.clientapi.entity.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'accès aux données des utilisateurs.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Recherche un utilisateur par son email.
     */
    Optional<User> findByEmail(String email);

    /**
     * Vérifie si un email existe déjà en base.
     */
    boolean existsByEmail(String email);

    /**
     * Vérifie si un email existe pour un autre utilisateur.
     */
    boolean existsByEmailAndIdNot(String email, Long id);

    /**
     * Recherche les utilisateurs par statut.
     */
    List<User> findByStatus(UserStatus status);
    Page<User> findByStatus(UserStatus status, Pageable pageable);

    /**
     * Recherche les utilisateurs par rôle.
     */
    List<User> findByRole(UserRole role);
    Page<User> findByRole(UserRole role, Pageable pageable);

    /**
     * Recherche les clients uniquement (rôle CLIENT).
     */
    @Query("SELECT u FROM User u WHERE u.role = 'CLIENT'")
    Page<User> findAllClients(Pageable pageable);

    /**
     * Recherche les administrateurs uniquement (rôle ADMIN).
     */
    @Query("SELECT u FROM User u WHERE u.role = 'ADMIN'")
    Page<User> findAllAdmins(Pageable pageable);

    /**
     * Recherche globale dans nom, prénom et email.
     */
    @Query("SELECT u FROM User u WHERE " +
            "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(u.companyName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<User> findBySearchTerm(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Recherche les utilisateurs créés après une date donnée.
     */
    List<User> findByCreatedAtAfter(LocalDateTime date);

    /**
     * Recherche les utilisateurs par ville.
     */
    List<User> findByCityIgnoreCase(String city);

    /**
     * Compte le nombre d'utilisateurs par statut.
     */
    long countByStatus(UserStatus status);

    /**
     * Compte le nombre d'utilisateurs par rôle.
     */
    long countByRole(UserRole role);

    /**
     * Recherche avec critères multiples.
     */
    @Query("SELECT u FROM User u WHERE " +
            "(:status IS NULL OR u.status = :status) AND " +
            "(:role IS NULL OR u.role = :role) AND " +
            "(:city IS NULL OR LOWER(u.city) = LOWER(:city))")
    Page<User> findByCriteria(
            @Param("status") UserStatus status,
            @Param("role") UserRole role,
            @Param("city") String city,
            Pageable pageable
    );
}