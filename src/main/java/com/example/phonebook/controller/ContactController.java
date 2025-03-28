package com.example.phonebook.controller;

import com.example.phonebook.controller.request.CreateContactRequestDto;
import com.example.phonebook.controller.request.UpdateContactRequestDto;
import com.example.phonebook.controller.response.ContactResponseDto;
import com.example.phonebook.controller.response.ErrorResponseDto;
import com.example.phonebook.entity.User;
import com.example.phonebook.exception.ContactNotFoundException;
import com.example.phonebook.exception.UserNotFoundException;
import com.example.phonebook.mapper.ContactMapper;
import com.example.phonebook.entity.Contact;
import com.example.phonebook.service.ContactService;
import com.example.phonebook.service.UserService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotEmpty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import jakarta.validation.Valid;
import java.util.Optional;

@Tag(name = "Contact Management", description = "Endpoints for managing user contacts")
@RestController
@Validated
@RequestMapping("/api/contacts")
public class ContactController {

    private static final Logger log = LoggerFactory.getLogger(ContactController.class);

    @Autowired
    private ContactService contactService;

    @Autowired
    private UserService userService;

    @Autowired
    private ContactMapper contactMapper;

    @Operation(summary = "Get Contacts", description = "Retrieve all contacts for a user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contacts retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ContactResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @GetMapping
    public ResponseEntity<Page<ContactResponseDto>> getContacts(@Parameter(description = "Search by first name or last name or phone number", example = "Naor")
                                                                    @RequestParam(required = false) String query,
                                                                @RequestParam(defaultValue = "0") Integer page,
                                                                @RequestParam(defaultValue = "10") @Max(10) Integer limit,
                                                                @Parameter(description = "User id", example = "67e4811cd360545f4342c843")
                                                                    @RequestParam @NotEmpty(message = "userId is required") String userId) {
        Pageable pageable = PageRequest.of(page, limit);
        log.info("Fetching contacts - userId: {}, page: {}, size: {}", userId, page, limit);
        Optional<User> userOptional = userService.getById(userId);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException(userId);
        }
        Page<Contact> contacts;
        if (query != null) {
            log.info("Search contact with the query {}", query);
            contacts = contactService.searchContactsByUserId(userId ,query, pageable);
        } else {
            contacts = contactService.getContactsByUserId(userId, pageable);
        }
        Page<ContactResponseDto> contactDtos = contactMapper.toDtoPage(contacts);
        log.info("Fetched {} contacts.", contacts.getTotalElements());
        return ResponseEntity.ok(contactDtos);
    }

    @Operation(summary = "Create Contact", description = "Add a new contact for a user")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Contact created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ContactResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PostMapping
    public ResponseEntity<ContactResponseDto> addContact(@Parameter(description = "User id", example = "67e4811cd360545f4342c843")
                                                             @RequestParam @NotEmpty(message = "userId is required") String userId,
                                                         @RequestBody @Valid CreateContactRequestDto contactRequestDto) {
        log.info("Adding new contact: {}", contactRequestDto);
        Optional<User> userOptional = userService.getById(userId);
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException(userId);
        }
        Contact contact = contactMapper.toEntity(userId, contactRequestDto);
        Contact savedContact = contactService.create(contact);
        log.info("Contact added with id: {}", savedContact.getId());
        ContactResponseDto contactResponseDto = contactMapper.toDto(savedContact);
        return new ResponseEntity<>(contactResponseDto, HttpStatus.CREATED);
    }

    @Operation(summary = "Update Contact", description = "Update an existing contact for a user")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Contact updated successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ContactResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Contact not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @PutMapping("/{id}")
    public ResponseEntity<ContactResponseDto> updateContact(@Parameter(description = "Contact id", example = "67e4811cd360545f4342c843")
                                                                @PathVariable String id,
                                                            @RequestBody @Valid UpdateContactRequestDto contactRequestDto) {
        log.info("Editing contact with id: {}", id);
        Optional<Contact> contactOptional = contactService.getById(id);
        if (contactOptional.isEmpty()) {
            throw new ContactNotFoundException(id);
        }
        Contact contact = contactMapper.toEntity(contactOptional.get().getUserId() ,contactRequestDto);
        Optional<Contact> updatedContact = contactService.update(id, contact);
        return updatedContact
                .map(myContact -> ResponseEntity.ok(contactMapper.toDto(myContact)))
                .orElseThrow(() -> new ContactNotFoundException(id));
    }

    @Operation(summary = "Delete Contact", description = "Delete a contact by ID")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Contact deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Contact not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponseDto.class)))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContact(@Parameter(description = "Contact id", example = "67e4811cd360545f4342c843")
                                                  @PathVariable String id) {
        log.info("Deleting contact with id: {}", id);
        boolean isDeleted = contactService.delete(id);
        if (isDeleted) {
            log.info("Contact with id {} deleted successfully.", id);
            return ResponseEntity.noContent().build();
        } else {
            throw new ContactNotFoundException(id);
        }
    }
}
