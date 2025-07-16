package com.example.clientapi;

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
import com.example.clientapi.service.AuthService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Tests")
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;
    private AdminRegisterRequest adminRegisterRequest;

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

        loginRequest = new LoginRequest();
        loginRequest.setEmail("john.doe@test.com");
        loginRequest.setPassword("password123");

        registerRequest = new RegisterRequest();
        registerRequest.setFirstName("John");
        registerRequest.setLastName("Doe");
        registerRequest.setEmail("john.doe@test.com");
        registerRequest.setPassword("password123");
        registerRequest.setPhone("0123456789");
        registerRequest.setCity("Paris");

        adminRegisterRequest = new AdminRegisterRequest();
        adminRegisterRequest.setFirstName("Admin");
        adminRegisterRequest.setLastName("User");
        adminRegisterRequest.setEmail("admin@test.com");
        adminRegisterRequest.setPassword("admin123");
        adminRegisterRequest.setRole(UserRole.ADMIN);
    }

    @Test
    @DisplayName("Should authenticate user successfully")
    void authenticateUser_ShouldReturnAuthResponse_WhenValidCredentials() {
        // Given
        String jwtToken = "jwt.token.here";
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
        when(jwtUtils.generateJwtToken(authentication)).thenReturn(jwtToken);

        // When
        AuthResponse result = authService.authenticateUser(loginRequest);

        // Then
        assertNotNull(result);
        assertEquals(jwtToken, result.getToken());
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getEmail(), result.getEmail());
        assertEquals(testUser.getFirstName(), result.getFirstName());
        assertEquals(testUser.getLastName(), result.getLastName());
        assertEquals(testUser.getRole(), result.getRole());
        assertEquals("Bearer", result.getType());

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtils).generateJwtToken(authentication);
    }

    @Test
    @DisplayName("Should register client successfully")
    void registerClient_ShouldReturnAuthResponse_WhenValidData() {
        // Given
        String jwtToken = "jwt.token.here";
        
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtUtils.generateTokenFromUsername(testUser.getEmail())).thenReturn(jwtToken);

        // When
        AuthResponse result = authService.registerClient(registerRequest);

        // Then
        assertNotNull(result);
        assertEquals(jwtToken, result.getToken());
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getEmail(), result.getEmail());
        assertEquals(testUser.getFirstName(), result.getFirstName());
        assertEquals(testUser.getLastName(), result.getLastName());
        assertEquals(UserRole.CLIENT, result.getRole());

        verify(userRepository).existsByEmail(registerRequest.getEmail());
        verify(passwordEncoder).encode(registerRequest.getPassword());
        verify(userRepository).save(any(User.class));
        verify(jwtUtils).generateTokenFromUsername(testUser.getEmail());
    }

    @Test
    @DisplayName("Should throw exception when registering client with existing email")
    void registerClient_ShouldThrowException_WhenEmailExists() {
        // Given
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        // When & Then
        EmailAlreadyExistsException exception = assertThrows(
            EmailAlreadyExistsException.class,
            () -> authService.registerClient(registerRequest)
        );

        assertEquals("Un utilisateur avec cet email existe déjà", exception.getMessage());
        verify(userRepository).existsByEmail(registerRequest.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should register admin successfully")
    void registerAdmin_ShouldReturnAuthResponse_WhenValidData() {
        // Given
        String jwtToken = "jwt.token.here";
        User adminUser = new User();
        adminUser.setId(2L);
        adminUser.setFirstName("Admin");
        adminUser.setLastName("User");
        adminUser.setEmail("admin@test.com");
        adminUser.setRole(UserRole.ADMIN);
        adminUser.setStatus(UserStatus.ACTIVE);
        
        when(userRepository.existsByEmail(adminRegisterRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(adminRegisterRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(adminUser);
        when(jwtUtils.generateTokenFromUsername(adminUser.getEmail())).thenReturn(jwtToken);

        // When
        AuthResponse result = authService.registerAdmin(adminRegisterRequest);

        // Then
        assertNotNull(result);
        assertEquals(jwtToken, result.getToken());
        assertEquals(adminUser.getId(), result.getId());
        assertEquals(adminUser.getEmail(), result.getEmail());
        assertEquals(adminUser.getFirstName(), result.getFirstName());
        assertEquals(adminUser.getLastName(), result.getLastName());
        assertEquals(UserRole.ADMIN, result.getRole());

        verify(userRepository).existsByEmail(adminRegisterRequest.getEmail());
        verify(passwordEncoder).encode(adminRegisterRequest.getPassword());
        verify(userRepository).save(any(User.class));
        verify(jwtUtils).generateTokenFromUsername(adminUser.getEmail());
    }

    @Test
    @DisplayName("Should throw exception when registering admin with existing email")
    void registerAdmin_ShouldThrowException_WhenEmailExists() {
        // Given
        when(userRepository.existsByEmail(adminRegisterRequest.getEmail())).thenReturn(true);

        // When & Then
        EmailAlreadyExistsException exception = assertThrows(
            EmailAlreadyExistsException.class,
            () -> authService.registerAdmin(adminRegisterRequest)
        );

        assertEquals("Un utilisateur avec cet email existe déjà", exception.getMessage());
        verify(userRepository).existsByEmail(adminRegisterRequest.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should check if email exists")
    void emailExists_ShouldReturnTrue_WhenEmailExists() {
        // Given
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        // When
        boolean result = authService.emailExists("test@example.com");

        // Then
        assertTrue(result);
        verify(userRepository).existsByEmail("test@example.com");
    }

    @Test
    @DisplayName("Should return false when email does not exist")
    void emailExists_ShouldReturnFalse_WhenEmailDoesNotExist() {
        // Given
        when(userRepository.existsByEmail("nonexistent@example.com")).thenReturn(false);

        // When
        boolean result = authService.emailExists("nonexistent@example.com");

        // Then
        assertFalse(result);
        verify(userRepository).existsByEmail("nonexistent@example.com");
    }
}