package com.example.phonebook.service;

import com.example.phonebook.entity.Contact;
import com.example.phonebook.entity.User;
import com.example.phonebook.exception.UserNotFoundException;
import com.example.phonebook.repository.ContactRepository;
import com.example.phonebook.service.UserService;
import com.example.phonebook.service.impl.ContactServiceImpl;
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
class ContactServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private ContactRepository contactRepository;

    @InjectMocks
    private ContactServiceImpl contactService;

    private Contact contact;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId("1");
        user.setFirstName("Naor");
        user.setLastName("Fahima");

        contact = new Contact();
        contact.setId("100");
        contact.setUserId("1");
        contact.setFirstName("Idan");
        contact.setLastName("Dor");
        contact.setPhoneNumber("9876543210");
    }

    @Test
    void testGetContactsByUserId() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Contact> page = new PageImpl<>(List.of(contact));
        when(contactRepository.findByUserId("1", pageable)).thenReturn(page);

        Page<Contact> result = contactService.getContactsByUserId("1", pageable);
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void testSearchContactsByUserId() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Contact> page = new PageImpl<>(List.of(contact));
        when(contactRepository.searchByUserIdAndQuery("1", "Idan", pageable)).thenReturn(page);

        Page<Contact> result = contactService.searchContactsByUserId("1", "Idan", pageable);
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void testGetAll() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Contact> page = new PageImpl<>(List.of(contact));
        when(contactRepository.findAll(pageable)).thenReturn(page);

        Page<Contact> result = contactService.getAll(pageable);
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void testGetById() {
        when(contactRepository.findById("100")).thenReturn(Optional.of(contact));

        Optional<Contact> result = contactService.getById("100");
        assertTrue(result.isPresent());
        assertEquals("Idan", result.get().getFirstName());
        assertEquals("Dor", result.get().getLastName());

    }

    @Test
    void testCreate_Success() {
        when(userService.getById("1")).thenReturn(Optional.of(user));
        when(contactRepository.save(contact)).thenReturn(contact);

        Contact result = contactService.create(contact);
        assertNotNull(result);
        assertEquals("Idan", result.getFirstName());
        assertEquals("Dor", result.getLastName());

    }

    @Test
    void testCreate_UserNotFound() {
        when(userService.getById("1")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> contactService.create(contact));
    }

    @Test
    void testUpdate_Success() {
        Contact updatedContact = new Contact();
        updatedContact.setFirstName("Jane");
        updatedContact.setLastName("Smith");
        updatedContact.setPhoneNumber("1234567890");
        updatedContact.setUserId("1");

        when(contactRepository.findById("100")).thenReturn(Optional.of(contact));
        when(contactRepository.save(any(Contact.class))).thenReturn(updatedContact);

        Optional<Contact> result = contactService.update("100", updatedContact);
        assertTrue(result.isPresent());
        assertEquals("Jane", result.get().getFirstName());
        assertEquals("Smith", result.get().getLastName());

    }

    @Test
    void testUpdate_NotFound() {
        when(contactRepository.findById("100")).thenReturn(Optional.empty());

        Optional<Contact> result = contactService.update("100", contact);
        assertFalse(result.isPresent());
    }

    @Test
    void testDelete_Success() {
        when(contactRepository.existsById("100")).thenReturn(true);
        doNothing().when(contactRepository).deleteById("100");

        boolean result = contactService.delete("100");
        assertTrue(result);
    }

    @Test
    void testDelete_NotFound() {
        when(contactRepository.existsById("100")).thenReturn(false);

        boolean result = contactService.delete("100");
        assertFalse(result);
    }
}
