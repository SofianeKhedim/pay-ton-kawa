package com.example.clientapi.service.impl;

import com.example.clientapi.dto.UserDto;
import com.example.clientapi.dto.CreateUserDto;
import com.example.clientapi.dto.UpdateUserDto;
import com.example.clientapi.entity.User;
import com.example.clientapi.entity.UserStatus;
import com.example.clientapi.entity.UserRole;
import com.example.clientapi.exception.UserNotFoundException;
import com.example.clientapi.exception.EmailAlreadyExistsException;
import com.example.clientapi.repository.UserRepository;
import com.example.clientapi.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implémentation du service de gestion des utilisateurs avec sécurité.
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDto createUser(CreateUserDto createUserDto) {
        logger.info("Création d'un nouvel utilisateur avec l'email: {} et le rôle: {}",
                createUserDto.getEmail(), createUserDto.getRole());

        // Vérification de l'unicité de l'email
        if (userRepository.existsByEmail(createUserDto.getEmail())) {
            logger.warn("Tentative de création d'un utilisateur avec un email existant: {}",
                    createUserDto.getEmail());
            throw new EmailAlreadyExistsException("Un utilisateur avec cet email existe déjà: " +
                    createUserDto.getEmail());
        }

        // Conversion DTO vers entité
        User user = convertCreateDtoToEntity(createUserDto);

        // Sauvegarde
        User savedUser = userRepository.save(user);
        logger.info("Utilisateur créé avec succès. ID: {}, Email: {}, Rôle: {}",
                savedUser.getId(), savedUser.getEmail(), savedUser.getRole());

        // Conversion entité vers DTO de réponse (sans mot de passe)
        return convertEntityToDto(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        logger.debug("Recherche de l'utilisateur avec l'ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Utilisateur non trouvé avec l'ID: {}", id);
                    return new UserNotFoundException("Utilisateur non trouvé avec l'ID: " + id);
                });

        logger.debug("Utilisateur trouvé: {} ({})", user.getEmail(), user.getRole());
        return convertEntityToDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserByEmail(String email) {
        logger.debug("Recherche de l'utilisateur avec l'email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("Utilisateur non trouvé avec l'email: {}", email);
                    return new UserNotFoundException("Utilisateur non trouvé avec l'email: " + email);
                });

        logger.debug("Utilisateur trouvé avec l'ID: {} ({})", user.getId(), user.getRole());
        return convertEntityToDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDto> getAllUsers(Pageable pageable) {
        logger.debug("Récupération de tous les utilisateurs. Page: {}, Taille: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<User> usersPage = userRepository.findAll(pageable);
        logger.debug("Nombre d'utilisateurs trouvés: {}", usersPage.getTotalElements());

        return usersPage.map(this::convertEntityToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDto> getAllClients(Pageable pageable) {
        logger.debug("Récupération de tous les clients. Page: {}, Taille: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<User> clientsPage = userRepository.findAllClients(pageable);
        logger.debug("Nombre de clients trouvés: {}", clientsPage.getTotalElements());

        return clientsPage.map(this::convertEntityToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDto> getAllAdmins(Pageable pageable) {
        logger.debug("Récupération de tous les administrateurs. Page: {}, Taille: {}",
                pageable.getPageNumber(), pageable.getPageSize());

        Page<User> adminsPage = userRepository.findAllAdmins(pageable);
        logger.debug("Nombre d'administrateurs trouvés: {}", adminsPage.getTotalElements());

        return adminsPage.map(this::convertEntityToDto);
    }

    @Override
    public UserDto updateUser(Long id, UpdateUserDto updateUserDto) {
        logger.info("Mise à jour de l'utilisateur avec l'ID: {}", id);

        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("Utilisateur non trouvé pour mise à jour avec l'ID: {}", id);
                    return new UserNotFoundException("Utilisateur non trouvé avec l'ID: " + id);
                });

        // Vérification de l'unicité de l'email si modifié
        if (updateUserDto.getEmail() != null &&
                !updateUserDto.getEmail().equals(existingUser.getEmail())) {
            if (userRepository.existsByEmailAndIdNot(updateUserDto.getEmail(), id)) {
                logger.warn("Tentative de mise à jour avec un email existant: {}", updateUserDto.getEmail());
                throw new EmailAlreadyExistsException("Un utilisateur avec cet email existe déjà: " +
                        updateUserDto.getEmail());
            }
        }

        // Mise à jour des champs
        updateEntityFromDto(existingUser, updateUserDto);

        // Sauvegarde
        User updatedUser = userRepository.save(existingUser);
        logger.info("Utilisateur mis à jour avec succès. ID: {}", updatedUser.getId());

        return convertEntityToDto(updatedUser);
    }

    @Override
    public void deleteUser(Long id) {
        logger.info("Suppression de l'utilisateur avec l'ID: {}", id);

        if (!userRepository.existsById(id)) {
            logger.warn("Tentative de suppression d'un utilisateur inexistant avec l'ID: {}", id);
            throw new UserNotFoundException("Utilisateur non trouvé avec l'ID: " + id);
        }

        userRepository.deleteById(id);
        logger.info("Utilisateur supprimé avec succès. ID: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDto> getUsersByStatus(UserStatus status, Pageable pageable) {
        logger.debug("Recherche des utilisateurs avec le statut: {}", status);

        Page<User> usersPage = userRepository.findByStatus(status, pageable);
        logger.debug("Nombre d'utilisateurs trouvés avec le statut {}: {}", status, usersPage.getTotalElements());

        return usersPage.map(this::convertEntityToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDto> getUsersByRole(UserRole role, Pageable pageable) {
        logger.debug("Recherche des utilisateurs avec le rôle: {}", role);

        Page<User> usersPage = userRepository.findByRole(role, pageable);
        logger.debug("Nombre d'utilisateurs trouvés avec le rôle {}: {}", role, usersPage.getTotalElements());

        return usersPage.map(this::convertEntityToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDto> searchUsers(String searchTerm, Pageable pageable) {
        logger.debug("Recherche globale d'utilisateurs avec le terme: {}", searchTerm);

        Page<User> usersPage = userRepository.findBySearchTerm(searchTerm, pageable);
        logger.debug("Nombre d'utilisateurs trouvés pour '{}': {}", searchTerm, usersPage.getTotalElements());

        return usersPage.map(this::convertEntityToDto);
    }

    @Override
    public UserDto activateUser(Long id) {
        logger.info("Activation de l'utilisateur avec l'ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé avec l'ID: " + id));

        user.setStatus(UserStatus.ACTIVE);
        User updatedUser = userRepository.save(user);

        logger.info("Utilisateur activé avec succès. ID: {}", id);
        return convertEntityToDto(updatedUser);
    }

    @Override
    public UserDto deactivateUser(Long id) {
        logger.info("Désactivation de l'utilisateur avec l'ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé avec l'ID: " + id));

        user.setStatus(UserStatus.INACTIVE);
        User updatedUser = userRepository.save(user);

        logger.info("Utilisateur désactivé avec succès. ID: {}", id);
        return convertEntityToDto(updatedUser);
    }

    @Override
    public UserDto changeUserRole(Long id, UserRole newRole) {
        logger.info("Changement de rôle pour l'utilisateur avec l'ID: {} vers: {}", id, newRole);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Utilisateur non trouvé avec l'ID: " + id));

        UserRole oldRole = user.getRole();
        user.setRole(newRole);
        User updatedUser = userRepository.save(user);

        logger.info("Rôle changé avec succès pour l'utilisateur ID: {} de {} vers {}",
                id, oldRole, newRole);
        return convertEntityToDto(updatedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public long countUsers() {
        return userRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long countUsersByStatus(UserStatus status) {
        return userRepository.countByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public long countUsersByRole(UserRole role) {
        return userRepository.countByRole(role);
    }

    /**
     * Vérifie si l'utilisateur connecté est propriétaire de l'ID donné.
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isOwner(Long userId, String authenticatedEmail) {
        return userRepository.findById(userId)
                .map(user -> user.getEmail().equals(authenticatedEmail))
                .orElse(false);
    }

    // Méthodes utilitaires de conversion

    private User convertCreateDtoToEntity(CreateUserDto dto) {
        User user = new User();
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());

        // Encoder le mot de passe
        if (dto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        user.setPhone(dto.getPhone());
        user.setAddress(dto.getAddress());
        user.setCity(dto.getCity());
        user.setPostalCode(dto.getPostalCode());
        user.setCountry(dto.getCountry());
        user.setRole(dto.getRole());
        user.setCompanyName(dto.getCompanyName());
        user.setStatus(UserStatus.ACTIVE); // Statut par défaut
        return user;
    }

    private void updateEntityFromDto(User user, UpdateUserDto dto) {
        if (dto.getFirstName() != null) {
            user.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null) {
            user.setLastName(dto.getLastName());
        }
        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }
        if (dto.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        if (dto.getPhone() != null) {
            user.setPhone(dto.getPhone());
        }
        if (dto.getAddress() != null) {
            user.setAddress(dto.getAddress());
        }
        if (dto.getCity() != null) {
            user.setCity(dto.getCity());
        }
        if (dto.getPostalCode() != null) {
            user.setPostalCode(dto.getPostalCode());
        }
        if (dto.getCountry() != null) {
            user.setCountry(dto.getCountry());
        }
        if (dto.getStatus() != null) {
            user.setStatus(dto.getStatus());
        }
        if (dto.getRole() != null) {
            user.setRole(dto.getRole());
        }
        if (dto.getCompanyName() != null) {
            user.setCompanyName(dto.getCompanyName());
        }
    }

    private UserDto convertEntityToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        // Ne pas exposer le mot de passe dans le DTO
        dto.setPhone(user.getPhone());
        dto.setAddress(user.getAddress());
        dto.setCity(user.getCity());
        dto.setPostalCode(user.getPostalCode());
        dto.setCountry(user.getCountry());
        dto.setStatus(user.getStatus());
        dto.setRole(user.getRole());
        dto.setCompanyName(user.getCompanyName());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }
}