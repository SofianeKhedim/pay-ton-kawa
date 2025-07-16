package com.example.clientapi;

import com.example.clientapi.dto.CreateUserDto;
import com.example.clientapi.dto.UpdateUserDto;
import com.example.clientapi.dto.UserDto;
import com.example.clientapi.entity.User;
import com.example.clientapi.entity.UserRole;
import com.example.clientapi.entity.UserStatus;
import com.example.clientapi.exception.EmailAlreadyExistsException;
import com.example.clientapi.exception.UserNotFoundException;
import com.example.clientapi.repository.UserRepository;
import com.example.clientapi.service.impl.UserServiceImpl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;
    private CreateUserDto createUserDto;
    private UpdateUserDto updateUserDto;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setFirstName("John");
        testUser.setLastName("Doe");
        testUser.setEmail("john.doe@test.com");
        testUser.setPassword("encodedPassword");
        testUser.setRole(UserRole.CLIENT);
        testUser.setStatus(UserStatus.ACTIVE);
        testUser.setCreatedAt(LocalDateTime.now());
        testUser.setUpdatedAt(LocalDateTime.now());

        createUserDto = new CreateUserDto();
        createUserDto.setFirstName("John");
        createUserDto.setLastName("Doe");
        createUserDto.setEmail("john.doe@test.com");
        createUserDto.setPassword("password123");
        createUserDto.setRole(UserRole.CLIENT);

        updateUserDto = new UpdateUserDto();
        updateUserDto.setFirstName("Jane");
        updateUserDto.setLastName("Smith");
    }

    @Test
    @DisplayName("Should create user successfully")
    void createUser_ShouldCreateUser_WhenValidData() {
        // Given
        when(userRepository.existsByEmail(createUserDto.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(createUserDto.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        UserDto result = userService.createUser(createUserDto);

        // Then
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getEmail(), result.getEmail());
        assertEquals(testUser.getFirstName(), result.getFirstName());
        assertEquals(testUser.getLastName(), result.getLastName());
        assertEquals(testUser.getRole(), result.getRole());
        
        verify(userRepository).existsByEmail(createUserDto.getEmail());
        verify(passwordEncoder).encode(createUserDto.getPassword());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw EmailAlreadyExistsException when email exists")
    void createUser_ShouldThrowException_WhenEmailExists() {
        // Given
        when(userRepository.existsByEmail(createUserDto.getEmail())).thenReturn(true);

        // When & Then
        EmailAlreadyExistsException exception = assertThrows(
            EmailAlreadyExistsException.class,
            () -> userService.createUser(createUserDto)
        );

        assertTrue(exception.getMessage().contains("Un utilisateur avec cet email existe déjà"));
        verify(userRepository).existsByEmail(createUserDto.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should get user by ID successfully")
    void getUserById_ShouldReturnUser_WhenUserExists() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        UserDto result = userService.getUserById(1L);

        // Then
        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getEmail(), result.getEmail());
        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user not found by ID")
    void getUserById_ShouldThrowException_WhenUserNotFound() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        UserNotFoundException exception = assertThrows(
            UserNotFoundException.class,
            () -> userService.getUserById(1L)
        );

        assertTrue(exception.getMessage().contains("Utilisateur non trouvé avec l'ID: 1"));
        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("Should get user by email successfully")
    void getUserByEmail_ShouldReturnUser_WhenUserExists() {
        // Given
        when(userRepository.findByEmail("john.doe@test.com")).thenReturn(Optional.of(testUser));

        // When
        UserDto result = userService.getUserByEmail("john.doe@test.com");

        // Then
        assertNotNull(result);
        assertEquals(testUser.getEmail(), result.getEmail());
        verify(userRepository).findByEmail("john.doe@test.com");
    }

    @Test
    @DisplayName("Should update user successfully")
    void updateUser_ShouldUpdateUser_WhenValidData() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        UserDto result = userService.updateUser(1L, updateUserDto);

        // Then
        assertNotNull(result);
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent user")
    void updateUser_ShouldThrowException_WhenUserNotFound() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        UserNotFoundException exception = assertThrows(
            UserNotFoundException.class,
            () -> userService.updateUser(1L, updateUserDto)
        );

        assertTrue(exception.getMessage().contains("Utilisateur non trouvé avec l'ID: 1"));
        verify(userRepository).findById(1L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should delete user successfully")
    void deleteUser_ShouldDeleteUser_WhenUserExists() {
        // Given
        when(userRepository.existsById(1L)).thenReturn(true);

        // When
        userService.deleteUser(1L);

        // Then
        verify(userRepository).existsById(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent user")
    void deleteUser_ShouldThrowException_WhenUserNotFound() {
        // Given
        when(userRepository.existsById(1L)).thenReturn(false);

        // When & Then
        UserNotFoundException exception = assertThrows(
            UserNotFoundException.class,
            () -> userService.deleteUser(1L)
        );

        assertTrue(exception.getMessage().contains("Utilisateur non trouvé avec l'ID: 1"));
        verify(userRepository).existsById(1L);
        verify(userRepository, never()).deleteById(1L);
    }

    @Test
    @DisplayName("Should get all users with pagination")
    void getAllUsers_ShouldReturnPagedUsers() {
        // Given
        List<User> users = Arrays.asList(testUser);
        Page<User> page = new PageImpl<>(users);
        Pageable pageable = PageRequest.of(0, 20);
        
        when(userRepository.findAll(pageable)).thenReturn(page);

        // When
        Page<UserDto> result = userService.getAllUsers(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testUser.getEmail(), result.getContent().get(0).getEmail());
        verify(userRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Should get clients only")
    void getAllClients_ShouldReturnClientsOnly() {
        // Given
        List<User> clients = Arrays.asList(testUser);
        Page<User> page = new PageImpl<>(clients);
        Pageable pageable = PageRequest.of(0, 20);
        
        when(userRepository.findAllClients(pageable)).thenReturn(page);

        // When
        Page<UserDto> result = userService.getAllClients(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(userRepository).findAllClients(pageable);
    }

    @Test
    @DisplayName("Should activate user successfully")
    void activateUser_ShouldActivateUser_WhenUserExists() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        UserDto result = userService.activateUser(1L);

        // Then
        assertNotNull(result);
        assertEquals(UserStatus.ACTIVE, testUser.getStatus());
        verify(userRepository).findById(1L);
        verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("Should deactivate user successfully")
    void deactivateUser_ShouldDeactivateUser_WhenUserExists() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        UserDto result = userService.deactivateUser(1L);

        // Then
        assertNotNull(result);
        verify(userRepository).findById(1L);
        verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("Should change user role successfully")
    void changeUserRole_ShouldChangeRole_WhenUserExists() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        UserDto result = userService.changeUserRole(1L, UserRole.ADMIN);

        // Then
        assertNotNull(result);
        assertEquals(UserRole.ADMIN, testUser.getRole());
        verify(userRepository).findById(1L);
        verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("Should search users by term")
    void searchUsers_ShouldReturnMatchingUsers() {
        // Given
        List<User> users = Arrays.asList(testUser);
        Page<User> page = new PageImpl<>(users);
        Pageable pageable = PageRequest.of(0, 20);
        String searchTerm = "john";
        
        when(userRepository.findBySearchTerm(searchTerm, pageable)).thenReturn(page);

        // When
        Page<UserDto> result = userService.searchUsers(searchTerm, pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(userRepository).findBySearchTerm(searchTerm, pageable);
    }

    @Test
    @DisplayName("Should check if email exists")
    void emailExists_ShouldReturnTrue_WhenEmailExists() {
        // Given
        when(userRepository.existsByEmail("john.doe@test.com")).thenReturn(true);

        // When
        boolean result = userService.emailExists("john.doe@test.com");

        // Then
        assertTrue(result);
        verify(userRepository).existsByEmail("john.doe@test.com");
    }

    @Test
    @DisplayName("Should count users correctly")
    void countUsers_ShouldReturnCorrectCount() {
        // Given
        when(userRepository.count()).thenReturn(5L);

        // When
        long result = userService.countUsers();

        // Then
        assertEquals(5L, result);
        verify(userRepository).count();
    }

    @Test
    @DisplayName("Should verify user ownership correctly")
    void isOwner_ShouldReturnTrue_WhenUserIsOwner() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        boolean result = userService.isOwner(1L, "john.doe@test.com");

        // Then
        assertTrue(result);
        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("Should return false for ownership when emails don't match")
    void isOwner_ShouldReturnFalse_WhenEmailsDontMatch() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        // When
        boolean result = userService.isOwner(1L, "other@test.com");

        // Then
        assertFalse(result);
        verify(userRepository).findById(1L);
    }
}