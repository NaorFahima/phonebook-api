package com.example.phonebook.controller;

import com.example.phonebook.controller.request.CreateUserRequestDto;
import com.example.phonebook.controller.request.UpdateUserRequestDto;
import com.example.phonebook.controller.response.ErrorResponseDto;
import com.example.phonebook.controller.response.UserResponseDto;
import com.example.phonebook.exception.UserAlreadyExitsException;
import com.example.phonebook.exception.UserNotFoundException;
import com.example.phonebook.mapper.UserMapper;
import com.example.phonebook.entity.User;
import com.example.phonebook.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@Tag(name = "User Management", description = "Endpoints for managing users")
@RestController
@Validated
@RequestMapping("/api/users")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    @Operation(summary = "Get User by ID", description = "Retrieve user details by their unique ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUser(@Parameter(description = "User id", example = "67e4811cd360545f4342c843")
                                                       @PathVariable String id) {
        log.info("Fetching user with id: {}", id);
        Optional<User> user = userService.getById(id);
        return user
                .map(myUser -> ResponseEntity.ok(userMapper.toDto(myUser)))
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Operation(summary = "Get all Users", description = "Retrieve a paginated list of all users")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid pagination parameters",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @GetMapping
    public ResponseEntity<Page<UserResponseDto>> getUsers(@Parameter(description = "Phone number", example = "0526572266")
                                                              @RequestParam(required = false) String phoneNumber,
                                                          @RequestParam(defaultValue = "0") Integer page,
                                                          @RequestParam(defaultValue = "10") @Max(10) Integer limit) {
        Pageable pageable = PageRequest.of(page, limit);
        log.info("Fetching users - page: {}, size: {}", page, limit);
        Page<User> usersPage;
        if (phoneNumber != null) {
            usersPage = userService.getByPhoneNumber(phoneNumber, pageable);
        } else {
            usersPage = userService.getAll(pageable);
        }

        Page<UserResponseDto> userResponseDtos = userMapper.toDtoPage(usersPage);
        log.info("Successfully fetch number of users {}",userResponseDtos.getTotalElements());
        return ResponseEntity.ok(userResponseDtos);
    }

    @Operation(summary = "Create a new User", description = "Create a new user with the provided information")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid user data provided",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class))),
            @ApiResponse(responseCode = "409", description = "User already exists with the given phone number",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping
    public ResponseEntity<UserResponseDto> createUser(@RequestBody @Valid CreateUserRequestDto userRequestDto) {
        log.info("Creating new user: {}", userRequestDto);
        Optional<User> userOptional = userService.getByPhoneNumber(userRequestDto.getPhoneNumber());
        if (userOptional.isPresent()) {
            throw new UserAlreadyExitsException(userRequestDto.getPhoneNumber());
        }
        User user = userMapper.toEntity(userRequestDto);
        User savedUser = userService.create(user);
        log.info("User created with id: {}", savedUser.getId());
        return ResponseEntity.ok(userMapper.toDto(savedUser));
    }

    @Operation(summary = "Update User by ID", description = "Update user details by their unique ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "User updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDto> updateUser(@Parameter(description = "User id", example = "67e4811cd360545f4342c843")
                                                          @PathVariable String id,
                                                      @RequestBody @Valid UpdateUserRequestDto userRequestDto) {
        log.info("Updating user with id: {}", id);
        User user = userMapper.toEntity(userRequestDto);
        user.setId(id);
        return userService.update(id, user)
                .map(updatedUser -> ResponseEntity.ok(userMapper.toDto(updatedUser)))
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Operation(summary = "Delete User by ID", description = "Delete a user by their unique ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@Parameter(description = "User id", example = "67e4811cd360545f4342c843")
                                               @PathVariable String id) {
        log.info("Deleting user with id: {}", id);
        boolean isDeleted = userService.delete(id);
        if (isDeleted) {
            log.info("User with id {} deleted successfully.", id);
            return ResponseEntity.noContent().build();
        } else {
            throw new UserNotFoundException(id);
        }
    }
}
