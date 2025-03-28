package com.example.phonebook.service.impl;

import com.example.phonebook.entity.Contact;
import com.example.phonebook.entity.User;
import com.example.phonebook.exception.UserNotFoundException;
import com.example.phonebook.repository.ContactRepository;
import com.example.phonebook.service.ContactService;
import com.example.phonebook.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ContactServiceImpl implements ContactService {

    @Autowired
    private UserService userService;

    @Autowired
    private ContactRepository contactRepository;

    @Override
    public Page<Contact> getContactsByUserId(String userId, Pageable pageable) {
        return contactRepository.findByUserId(userId, pageable);
    }

    @Override
    public Page<Contact> searchContactsByUserId(String userId, String query, Pageable pageable) {
        return contactRepository.searchByUserIdAndQuery(userId, query, pageable);
    }

    @Override
    public Page<Contact> getAll(Pageable pageable) {
        return contactRepository.findAll(pageable);
    }

    @Override
    public Optional<Contact> getById(String id) {
        return contactRepository.findById(id);
    }

    @Override
    public Contact create(Contact entity) {
        Optional<User> user = userService.getById(entity.getUserId());
        if (user.isEmpty()) {
            throw new UserNotFoundException(entity.getUserId());
        }
        return contactRepository.save(entity);
    }

    @Override
    public Optional<Contact> update(String id, Contact entity) {
        Optional<Contact> existingContact = contactRepository.findById(id);
        if (existingContact.isPresent()) {
            entity.setId(id);
            entity.setCreateAt(existingContact.get().getCreateAt());
            return Optional.of(contactRepository.save(entity));
        }
        return Optional.empty();
    }

    @Override
    public boolean delete(String id) {
        if (contactRepository.existsById(id)) {
            contactRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
