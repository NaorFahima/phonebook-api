package com.example.phonebook.controller;

import com.example.phonebook.controller.request.CreateContactRequestDto;
import com.example.phonebook.controller.request.UpdateContactRequestDto;
import com.example.phonebook.controller.response.ContactResponseDto;
import com.example.phonebook.entity.Contact;
import com.example.phonebook.entity.User;
import com.example.phonebook.exception.ContactNotFoundException;
import com.example.phonebook.exception.UserNotFoundException;
import com.example.phonebook.mapper.ContactMapper;
import com.example.phonebook.service.ContactService;
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
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContactControllerTest {

    @Mock
    private ContactService contactService;

    @Mock
    private UserService userService;

    @Mock
    private ContactMapper contactMapper;

    @InjectMocks
    private ContactController contactController;

    private User user;
    private Contact contact;
    private ContactResponseDto contactResponseDto;
    private CreateContactRequestDto createContactRequestDto;
    private UpdateContactRequestDto updateContactRequestDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId("user123");

        contact = new Contact();
        contact.setId("contact123");
        contact.setUserId("user123");

        contactResponseDto = new ContactResponseDto();
        contactResponseDto.setId("contact123");

        createContactRequestDto = new CreateContactRequestDto();
        updateContactRequestDto = new UpdateContactRequestDto();
    }

    @Test
    void getContacts_ShouldReturnPagedContacts() {
        Page<Contact> contactPage = new PageImpl<>(Collections.singletonList(contact));
        when(userService.getById("user123")).thenReturn(Optional.of(user));
        when(contactService.getContactsByUserId(eq("user123"), any(PageRequest.class))).thenReturn(contactPage);
        when(contactMapper.toDtoPage(contactPage)).thenReturn(new PageImpl<>(Collections.singletonList(contactResponseDto)));

        ResponseEntity<Page<ContactResponseDto>> response = contactController.getContacts(null,0, 10, "user123");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertFalse(response.getBody().isEmpty());
    }

    @Test
    void getContacts_ShouldThrowException_WhenUserNotFound() {
        when(userService.getById("user123")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> contactController.getContacts(null,0, 10, "user123"));
    }

    @Test
    void addContact_ShouldCreateContact_WhenUserExists() {
        when(userService.getById("user123")).thenReturn(Optional.of(user));
        when(contactMapper.toEntity("user123", createContactRequestDto)).thenReturn(contact);
        when(contactService.create(contact)).thenReturn(contact);
        when(contactMapper.toDto(contact)).thenReturn(contactResponseDto);

        ResponseEntity<ContactResponseDto> response = contactController.addContact("user123", createContactRequestDto);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(contactResponseDto, response.getBody());
    }

    @Test
    void addContact_ShouldThrowException_WhenUserNotFound() {
        when(userService.getById("user123")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> contactController.addContact("user123", createContactRequestDto));
    }

    @Test
    void updateContact_ShouldUpdateContact_WhenContactExists() {
        when(contactService.getById("contact123")).thenReturn(Optional.of(contact));
        when(contactMapper.toEntity("user123", updateContactRequestDto)).thenReturn(contact);
        when(contactService.update(eq("contact123"), any(Contact.class))).thenReturn(Optional.of(contact));
        when(contactMapper.toDto(contact)).thenReturn(contactResponseDto);

        ResponseEntity<ContactResponseDto> response = contactController.updateContact("contact123", updateContactRequestDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(contactResponseDto, response.getBody());
    }

    @Test
    void updateContact_ShouldThrowException_WhenContactNotFound() {
        when(contactService.getById("contact123")).thenReturn(Optional.empty());

        assertThrows(ContactNotFoundException.class, () -> contactController.updateContact("contact123", updateContactRequestDto));
    }

    @Test
    void deleteContact_ShouldReturnNoContent_WhenContactDeleted() {
        when(contactService.delete("contact123")).thenReturn(true);

        ResponseEntity<Void> response = contactController.deleteContact("contact123");

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void deleteContact_ShouldThrowException_WhenContactNotFound() {
        when(contactService.delete("contact123")).thenReturn(false);

        assertThrows(ContactNotFoundException.class, () -> contactController.deleteContact("contact123"));
    }
}
