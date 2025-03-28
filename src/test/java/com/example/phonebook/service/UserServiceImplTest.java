package com.example.phonebook.service;

import com.example.phonebook.entity.User;
import com.example.phonebook.repository.UserRepository;
import com.example.phonebook.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId("1");
        user.setFirstName("Naor");
        user.setLastName("Fahima");
        user.setPhoneNumber("1234567890");
    }

    @Test
    void testGetAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> page = new PageImpl<>(List.of(user));
        when(userRepository.findAll(pageable)).thenReturn(page);

        Page<User> result = userService.getAll(pageable);
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void testGetById() {
        when(userRepository.findById("1")).thenReturn(Optional.of(user));

        Optional<User> result = userService.getById("1");
        assertTrue(result.isPresent());
        assertEquals("Naor", result.get().getFirstName());
        assertEquals("Fahima", result.get().getLastName());

    }

    @Test
    void testCreate() {
        when(userRepository.save(user)).thenReturn(user);

        User result = userService.create(user);
        assertNotNull(result);
        assertEquals("Naor", result.getFirstName());
        assertEquals("Fahima", result.getLastName());
    }

    @Test
    void testUpdate_Success() {
        User updatedUser = new User();
        updatedUser.setFirstName("Naor");
        updatedUser.setLastName("Fahima");
        updatedUser.setPhoneNumber("0987654321");

        when(userRepository.findById("1")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        Optional<User> result = userService.update("1", updatedUser);
        assertTrue(result.isPresent());
        assertEquals("Naor", result.get().getFirstName());
        assertEquals("Fahima", result.get().getLastName());
    }

    @Test
    void testUpdate_NotFound() {
        when(userRepository.findById("1")).thenReturn(Optional.empty());

        Optional<User> result = userService.update("1", user);
        assertFalse(result.isPresent());
    }

    @Test
    void testDelete_Success() {
        when(userRepository.findById("1")).thenReturn(Optional.of(user));
        doNothing().when(userRepository).deleteById("1");

        boolean result = userService.delete("1");
        assertTrue(result);
    }

    @Test
    void testDelete_NotFound() {
        when(userRepository.findById("1")).thenReturn(Optional.empty());

        boolean result = userService.delete("1");
        assertFalse(result);
    }

    @Test
    void testGetByPhoneNumber_Single() {
        when(userRepository.findByPhoneNumber("1234567890")).thenReturn(Optional.of(user));

        Optional<User> result = userService.getByPhoneNumber("1234567890");
        assertTrue(result.isPresent());
        assertEquals("Naor", result.get().getFirstName());
        assertEquals("Fahima", result.get().getLastName());
    }

    @Test
    void testGetByPhoneNumber_Paged() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> page = new PageImpl<>(List.of(user));
        when(userRepository.findByPhoneNumber("1234567890", pageable)).thenReturn(page);

        Page<User> result = userService.getByPhoneNumber("1234567890", pageable);
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }
}
