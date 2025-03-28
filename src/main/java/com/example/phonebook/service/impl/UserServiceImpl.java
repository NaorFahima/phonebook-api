package com.example.phonebook.service.impl;

import com.example.phonebook.entity.User;
import com.example.phonebook.repository.UserRepository;
import com.example.phonebook.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public Page<User> getAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public Optional<User> getById(String id) {
        return userRepository.findById(id);
    }

    @Override
    public User create(User entity) {
        return userRepository.save(entity);
    }

    @Override
    public Optional<User> update(String id, User entity) {
        Optional<User> existingUser = userRepository.findById(id);
        if (existingUser.isPresent()) {
            entity.setId(id);
            entity.setCreateAt(existingUser.get().getCreateAt());
            return Optional.of(userRepository.save(entity));
        }
        return Optional.empty();
    }

    @Override
    public boolean delete(String id) {
        Optional<User> existingUser = userRepository.findById(id);
        if (existingUser.isPresent()) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public Optional<User> getByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);
    }

    @Override
    public Page<User> getByPhoneNumber(String phoneNumber, Pageable pageable) {
        return userRepository.findByPhoneNumber(phoneNumber, pageable);
    }

}
