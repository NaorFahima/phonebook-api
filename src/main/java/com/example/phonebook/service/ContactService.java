package com.example.phonebook.service;

import com.example.phonebook.entity.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ContactService extends GenericService<Contact, String> {
    Page<Contact> getContactsByUserId(String userId, Pageable pageable);
    Page<Contact> searchContactsByUserId(String userId, String query, Pageable pageable);
}