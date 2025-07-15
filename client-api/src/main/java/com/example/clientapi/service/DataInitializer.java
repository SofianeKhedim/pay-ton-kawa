package com.example.clientapi.service;

import com.example.clientapi.entity.User;
import com.example.clientapi.entity.UserRole;
import com.example.clientapi.entity.UserStatus;
import com.example.clientapi.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Initialise les données par défaut au démarrage de l'application en mode développement.
 *
 * Cette classe s'exécute uniquement avec le profil 'dev' et crée automatiquement
 * des comptes administrateur et client pour faciliter les tests.
 */
@Component
// @Profile("dev") // S'exécute uniquement en mode développement
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        logger.info("🚀 Initialisation des données de développement...");

        initializeAdminUsers();
        initializeTestClients();

        logger.info("✅ Initialisation des données terminée !");
    }

    /**
     * Crée les comptes administrateur par défaut.
     */
    private void initializeAdminUsers() {
        // Administrateur principal
        createUserIfNotExists(
                "Super",
                "Admin",
                "admin@payetonkawa.com",
                "password123",
                UserRole.ADMIN,
                "Compte administrateur principal pour les tests"
        );

        // Administrateur secondaire pour les tests
        createUserIfNotExists(
                "Test",
                "Admin",
                "test.admin@payetonkawa.com",
                "admin123",
                UserRole.ADMIN,
                "Compte administrateur secondaire pour les tests"
        );
    }

    /**
     * Crée les comptes clients de test.
     */
    private void initializeTestClients() {
        // Client particulier
        createUserIfNotExists(
                "Jean",
                "Dupont",
                "client@test.com",
                "password123",
                UserRole.CLIENT,
                "Compte client particulier pour les tests"
        );

        // Client professionnel avec entreprise
        User professionalClient = createUserIfNotExists(
                "Marie",
                "Martin",
                "pro.client@test.com",
                "password123",
                UserRole.CLIENT,
                "Compte client professionnel pour les tests"
        );

        if (professionalClient != null && professionalClient.getCompanyName() == null) {
            professionalClient.setCompanyName("Restaurant Chez Marie");
            professionalClient.setPhone("0987654321");
            professionalClient.setAddress("45 Avenue des Champs");
            professionalClient.setCity("Lyon");
            professionalClient.setPostalCode("69000");
            professionalClient.setCountry("France");
            userRepository.save(professionalClient);
            logger.info("📝 Informations complémentaires ajoutées pour: {}", professionalClient.getEmail());
        }

        // Client inactif pour tester les statuts
        User inactiveClient = createUserIfNotExists(
                "Test",
                "Inactif",
                "inactive@test.com",
                "password123",
                UserRole.CLIENT,
                "Compte client inactif pour tester les statuts"
        );

        if (inactiveClient != null) {
            inactiveClient.setStatus(UserStatus.INACTIVE);
            userRepository.save(inactiveClient);
            logger.info("🔒 Statut défini sur INACTIVE pour: {}", inactiveClient.getEmail());
        }
    }

    /**
     * Crée un utilisateur s'il n'existe pas déjà.
     */
    private User createUserIfNotExists(String firstName, String lastName, String email,
                                       String password, UserRole role, String description) {

        if (userRepository.existsByEmail(email)) {
            logger.debug("⏭️ Utilisateur {} existe déjà, passage au suivant", email);
            return userRepository.findByEmail(email).orElse(null);
        }

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        user.setStatus(UserStatus.ACTIVE);

        User savedUser = userRepository.save(user);

        logger.info("✨ {} créé: {} ({}) - {}",
                role.name(),
                savedUser.getEmail(),
                savedUser.getFullName(),
                description);

        return savedUser;
    }
}