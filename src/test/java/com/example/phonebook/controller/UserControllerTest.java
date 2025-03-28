package com.example.phonebook.controller;

import com.example.phonebook.controller.request.CreateUserRequestDto;
import com.example.phonebook.controller.request.UpdateUserRequestDto;
import com.example.phonebook.controller.response.UserResponseDto;
import com.example.phonebook.entity.User;
import com.example.phonebook.exception.UserAlreadyExitsException;
import com.example.phonebook.exception.UserNotFoundException;
import com.example.phonebook.mapper.UserMapper;
import com.example.phonebook.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserController userController;

    private User user;
    private UserResponseDto userResponseDto;
    private CreateUserRequestDto createUserRequestDto;
    private UpdateUserRequestDto updateUserRequestDto;

    private Page<User> userPage;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId("123");
        user.setPhoneNumber("123456789");

        userResponseDto = new UserResponseDto();
        userResponseDto.setId("123");
        userResponseDto.setPhoneNumber("123456789");

        createUserRequestDto = new CreateUserRequestDto();
        createUserRequestDto.setPhoneNumber("123456789");

        updateUserRequestDto = new UpdateUserRequestDto();
        updateUserRequestDto.setPhoneNumber("987654321");

        userPage = new PageImpl<>(List.of(user));

    }

    @Test
    void getUser_ShouldReturnUser_WhenUserExists() {
        when(userService.getById("123")).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userResponseDto);

        ResponseEntity<UserResponseDto> response = userController.getUser("123");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userResponseDto, response.getBody());
    }

    @Test
    void getUser_ShouldThrowException_WhenUserNotFound() {
        when(userService.getById("123")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userController.getUser("123"));
    }

//    @Test
//    void getUsers_ShouldReturnPagedUsers() {
//            // Prepare Pageable
//            Pageable pageable = PageRequest.of(0, 10);
//
//            // Mock the UserService to return a Page of users
//            when(userService.getAll(pageable)).thenReturn(userPage);
//
//            // Mock the UserMapper to convert the Page<User> to Page<UserResponseDto>
//            when(userMapper.toDtoPage(userPage)).thenReturn(new PageImpl<>(List.of(userResponseDto)));
//
//            // Call the endpoint
//            ResponseEntity<Page<UserResponseDto>> response = userController.getUsers("123456789",0,10);
//
//            // Validate response
//            assertNotNull(response);
//            assertEquals(HttpStatus.OK, response.getStatusCode());  // Check if status is OK
//            assertNotNull(response.getBody());  // Ensure the body is not null
//            assertEquals(1, response.getBody().getContent().size());  // Ensure it contains one user
//            assertEquals("123", response.getBody().getContent().get(0).getId());  // Check user ID
//
//            // Verify interactions with mocks
//            verify(userService).getAll(pageable);  // Ensure userService.getAll(pageable) was called
//            verify(userMapper).toDtoPage(userPage);  // Ensure userMapper.toDtoPage(userPage) was called
//
//
////        Page<User> userPage = new PageImpl<>(Collections.singletonList(user));
////        when(userService.getAll(any(PageRequest.class))).thenReturn(userPage);
////        when(userMapper.toDtoPage(userPage)).thenReturn(new PageImpl<>(Collections.singletonList(userResponseDto)));
////
////        ResponseEntity<Page<UserResponseDto>> response = userController.getUsers("123456789",0, 10);
////
////        assertEquals(HttpStatus.OK, response.getStatusCode());
////        assertFalse(response.getBody().isEmpty());
//    }

    @Test
    void getUsers_ShouldReturnPagedUsers() {
        // Prepare the mock data
        Page<User> usersPage = new PageImpl<>(Collections.singletonList(user));
        Page<UserResponseDto> userResponseDtos = new PageImpl<>(Collections.singletonList(userResponseDto));

        // Mock the service layer
        when(userService.getByPhoneNumber(eq("0526572266"), any(PageRequest.class))).thenReturn(usersPage);
        when(userMapper.toDtoPage(usersPage)).thenReturn(userResponseDtos);

        // Perform the controller method
        ResponseEntity<Page<UserResponseDto>> response = userController.getUsers("0526572266", 0, 10);

        // Assertions to verify the result
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());
        assertEquals("123", response.getBody().getContent().get(0).getId());
    }

    @Test
    void getUsers_ShouldReturnPagedUsers_WhenPhoneNumberIsNull() {
        // Prepare the mock data
        Page<User> usersPage = new PageImpl<>(Collections.singletonList(user));
        Page<UserResponseDto> userResponseDtos = new PageImpl<>(Collections.singletonList(userResponseDto));

        // Mock the service layer
        when(userService.getAll(any(PageRequest.class))).thenReturn(usersPage);
        when(userMapper.toDtoPage(usersPage)).thenReturn(userResponseDtos);

        // Perform the controller method
        ResponseEntity<Page<UserResponseDto>> response = userController.getUsers(null, 0, 10);

        // Assertions to verify the result
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());
        assertEquals("123", response.getBody().getContent().get(0).getId());
    }

    @Test
    void createUser_ShouldCreateUser_WhenUserDoesNotExist() {
        when(userService.getByPhoneNumber(createUserRequestDto.getPhoneNumber())).thenReturn(Optional.empty());
        when(userMapper.toEntity(createUserRequestDto)).thenReturn(user);
        when(userService.create(user)).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userResponseDto);

        ResponseEntity<UserResponseDto> response = userController.createUser(createUserRequestDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userResponseDto, response.getBody());
    }

    @Test
    void createUser_ShouldThrowException_WhenUserAlreadyExists() {
        when(userService.getByPhoneNumber(createUserRequestDto.getPhoneNumber())).thenReturn(Optional.of(user));

        assertThrows(UserAlreadyExitsException.class, () -> userController.createUser(createUserRequestDto));
    }

    @Test
    void updateUser_ShouldUpdateUser_WhenUserExists() {
        when(userMapper.toEntity(updateUserRequestDto)).thenReturn(user);
        when(userService.update(eq("123"), any(User.class))).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userResponseDto);

        ResponseEntity<UserResponseDto> response = userController.updateUser("123", updateUserRequestDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userResponseDto, response.getBody());
    }

    @Test
    void updateUser_ShouldThrowException_WhenUserNotFound() {
        when(userMapper.toEntity(updateUserRequestDto)).thenReturn(user);
        when(userService.update(eq("123"), any(User.class))).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userController.updateUser("123", updateUserRequestDto));
    }

    @Test
    void deleteUser_ShouldReturnNoContent_WhenUserDeleted() {
        when(userService.delete("123")).thenReturn(true);

        ResponseEntity<Void> response = userController.deleteUser("123");

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void deleteUser_ShouldThrowException_WhenUserNotFound() {
        when(userService.delete("123")).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> userController.deleteUser("123"));
    }
}
