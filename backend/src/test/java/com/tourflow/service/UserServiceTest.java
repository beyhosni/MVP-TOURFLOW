package com.tourflow.service;

import com.tourflow.dto.UserDTO;
import com.tourflow.exception.ResourceNotFoundException;
import com.tourflow.exception.UserAlreadyExistsException;
import com.tourflow.model.Role;
import com.tourflow.model.User;
import com.tourflow.repository.UserRepository;
import com.tourflow.service.base.ServiceTestBase;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest extends ServiceTestBase {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User mockUser;

    @BeforeEach
    public void setUp() {
        super.setUp();
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setFirstName("John");
        mockUser.setLastName("Doe");
        mockUser.setEmail("john.doe@example.com");
        mockUser.setPassword("encodedPassword");
        mockUser.setRole(Role.USER);
        mockUser.setCreatedAt(LocalDateTime.now());
        mockUser.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    public void testGetUserById_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        // When
        User result = userService.getUserById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("john.doe@example.com", result.getEmail());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetUserById_NotFound() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(1L));
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetUserByEmail_Success() {
        // Given
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(mockUser));

        // When
        User result = userService.getUserByEmail("john.doe@example.com");

        // Then
        assertNotNull(result);
        assertEquals("john.doe@example.com", result.getEmail());
        verify(userRepository, times(1)).findByEmail("john.doe@example.com");
    }

    @Test
    public void testGetUserByEmail_NotFound() {
        // Given
        when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> userService.getUserByEmail("nonexistent@example.com"));
        verify(userRepository, times(1)).findByEmail("nonexistent@example.com");
    }

    @Test
    public void testGetAllUsers_Success() {
        // Given
        List<User> users = Arrays.asList(mockUser);
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> page = new PageImpl<>(users, pageable, 1);

        when(userRepository.findAll(any(Pageable.class))).thenReturn(page);

        // When
        Page<User> result = userService.getAllUsers(pageable);

        // Then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("john.doe@example.com", result.getContent().get(0).getEmail());
        verify(userRepository, times(1)).findAll(any(Pageable.class));
    }

    @Test
    public void testCreateUser_Success() {
        // Given
        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName("Jane");
        userDTO.setLastName("Smith");
        userDTO.setEmail("jane.smith@example.com");
        userDTO.setPassword("password123");

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // When
        User result = userService.createUser(userDTO);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(userRepository, times(1)).existsByEmail("jane.smith@example.com");
        verify(passwordEncoder, times(1)).encode("password123");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testCreateUser_EmailAlreadyExists() {
        // Given
        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName("Jane");
        userDTO.setLastName("Smith");
        userDTO.setEmail("john.doe@example.com"); // Email déjà existant
        userDTO.setPassword("password123");

        when(userRepository.existsByEmail("john.doe@example.com")).thenReturn(true);

        // When & Then
        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(userDTO));
        verify(userRepository, times(1)).existsByEmail("john.doe@example.com");
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testUpdateUser_Success() {
        // Given
        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName("John");
        userDTO.setLastName("Updated");
        userDTO.setEmail("john.updated@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        // When
        User result = userService.updateUser(1L, userDTO);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void testUpdateUser_NotFound() {
        // Given
        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName("John");
        userDTO.setLastName("Updated");
        userDTO.setEmail("john.updated@example.com");

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(1L, userDTO));
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    public void testDeleteUser_Success() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        doNothing().when(userRepository).delete(mockUser);

        // When
        userService.deleteUser(1L);

        // Then
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).delete(mockUser);
    }

    @Test
    public void testDeleteUser_NotFound() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(1L));
        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, never()).delete(any(User.class));
    }
}
