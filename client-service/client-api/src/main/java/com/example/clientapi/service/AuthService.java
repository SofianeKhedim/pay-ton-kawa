package com.example.clientapi.service;

import com.example.clientapi.dto.auth.AdminRegisterRequest;
import com.example.clientapi.dto.auth.AuthResponse;
import com.example.clientapi.dto.auth.LoginRequest;
import com.example.clientapi.dto.auth.RegisterRequest;
import com.example.clientapi.entity.User;
import com.example.clientapi.entity.UserRole;
import com.example.clientapi.entity.UserStatus;
import com.example.clientapi.exception.EmailAlreadyExistsException;
import com.example.clientapi.repository.UserRepository;
import com.example.clientapi.security.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service d'authentification pour la gestion des connexions et inscriptions.
 */
@Service
@Transactional
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtils jwtUtils;

    /**
     * Authentifie un utilisateur et retourne un token JWT.
     */
    public AuthResponse authenticateUser(LoginRequest loginRequest) {
        logger.info("Tentative de connexion pour l'email: {}", loginRequest.getEmail());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        User user = (User) authentication.getPrincipal();

        logger.info("Connexion réussie pour l'utilisateur: {} ({})", user.getEmail(), user.getRole());

        return new AuthResponse(jwt, user.getId(), user.getEmail(),
                user.getFirstName(), user.getLastName(), user.getRole());
    }

    /**
     * Inscrit un nouveau client.
     */
    public AuthResponse registerClient(RegisterRequest registerRequest) {
        logger.info("Inscription d'un nouveau client avec l'email: {}", registerRequest.getEmail());

        // Vérification de l'unicité de l'email
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            logger.warn("Tentative d'inscription avec un email existant: {}", registerRequest.getEmail());
            throw new EmailAlreadyExistsException("Un utilisateur avec cet email existe déjà");
        }

        // Création du nouvel utilisateur client
        User user = new User();
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setPhone(registerRequest.getPhone());
        user.setAddress(registerRequest.getAddress());
        user.setCity(registerRequest.getCity());
        user.setPostalCode(registerRequest.getPostalCode());
        user.setCountry(registerRequest.getCountry());
        user.setCompanyName(registerRequest.getCompanyName());
        user.setRole(UserRole.CLIENT); // Toujours CLIENT pour l'inscription publique
        user.setStatus(UserStatus.ACTIVE);

        User savedUser = userRepository.save(user);

        // Génération du token JWT
        String jwt = jwtUtils.generateTokenFromUsername(savedUser.getEmail());

        logger.info("Client inscrit avec succès. ID: {}, Email: {}", savedUser.getId(), savedUser.getEmail());

        return new AuthResponse(jwt, savedUser.getId(), savedUser.getEmail(),
                savedUser.getFirstName(), savedUser.getLastName(), savedUser.getRole());
    }

    /**
     * Inscrit un nouvel administrateur (réservé aux admins existants).
     */
    public AuthResponse registerAdmin(AdminRegisterRequest registerRequest) {
        logger.info("Inscription d'un nouvel administrateur avec l'email: {}", registerRequest.getEmail());

        // Vérification de l'unicité de l'email
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            logger.warn("Tentative d'inscription admin avec un email existant: {}", registerRequest.getEmail());
            throw new EmailAlreadyExistsException("Un utilisateur avec cet email existe déjà");
        }

        // Création du nouvel utilisateur admin
        User user = new User();
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(registerRequest.getRole());
        user.setStatus(UserStatus.ACTIVE);

        User savedUser = userRepository.save(user);

        // Génération du token JWT
        String jwt = jwtUtils.generateTokenFromUsername(savedUser.getEmail());

        logger.info("Administrateur inscrit avec succès. ID: {}, Email: {}, Rôle: {}",
                savedUser.getId(), savedUser.getEmail(), savedUser.getRole());

        return new AuthResponse(jwt, savedUser.getId(), savedUser.getEmail(),
                savedUser.getFirstName(), savedUser.getLastName(), savedUser.getRole());
    }

    /**
     * Vérifie si un email existe déjà.
     */
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }
}