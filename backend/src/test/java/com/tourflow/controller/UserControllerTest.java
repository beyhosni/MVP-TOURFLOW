package com.tourflow.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tourflow.controller.base.ControllerTestBase;
import com.tourflow.dto.UserDTO;
import com.tourflow.model.Role;
import com.tourflow.model.User;
import com.tourflow.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UserControllerTest extends ControllerTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private User mockUser;

    @Override
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
    @WithMockUser(roles = {"USER"})
    public void testGetUserById_Success() throws Exception {
        // Given
        when(userService.getUserById(1L)).thenReturn(mockUser);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get(API_PREFIX + "/users/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("john.doe@example.com"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void testGetAllUsers_Success() throws Exception {
        // Given
        List<User> users = Arrays.asList(mockUser);
        Page<User> page = new PageImpl<>(users, PageRequest.of(0, 10), 1);

        when(userService.getAllUsers(any())).thenReturn(page);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get(API_PREFIX + "/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].email").value("john.doe@example.com"));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void testCreateUser_Success() throws Exception {
        // Given
        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName("Jane");
        userDTO.setLastName("Smith");
        userDTO.setEmail("jane.smith@example.com");
        userDTO.setPassword("password123");

        when(userService.createUser(any(UserDTO.class))).thenReturn(mockUser);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post(API_PREFIX + "/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void testUpdateUser_Success() throws Exception {
        // Given
        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName("John");
        userDTO.setLastName("Updated");
        userDTO.setEmail("john.updated@example.com");

        when(userService.updateUser(anyLong(), any(UserDTO.class))).thenReturn(mockUser);

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.put(API_PREFIX + "/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = {"ADMIN"})
    public void testDeleteUser_Success() throws Exception {
        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.delete(API_PREFIX + "/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void testGetUserById_AccessDenied() throws Exception {
        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get(API_PREFIX + "/users/2"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void testUpdateUser_AccessDenied() throws Exception {
        // Given
        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName("John");
        userDTO.setLastName("Updated");
        userDTO.setEmail("john.updated@example.com");

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.put(API_PREFIX + "/users/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userDTO)))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void testDeleteUser_AccessDenied() throws Exception {
        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.delete(API_PREFIX + "/users/2"))
                .andExpect(status().isForbidden());
    }

    @Test
    public void testGetUserById_Unauthorized() throws Exception {
        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.get(API_PREFIX + "/users/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = {"USER"})
    public void testCreateUser_AccessDenied() throws Exception {
        // Given
        UserDTO userDTO = new UserDTO();
        userDTO.setFirstName("Jane");
        userDTO.setLastName("Smith");
        userDTO.setEmail("jane.smith@example.com");
        userDTO.setPassword("password123");

        // When & Then
        mockMvc.perform(MockMvcRequestBuilders.post(API_PREFIX + "/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userDTO)))
                .andExpect(status().isForbidden());
    }
}
