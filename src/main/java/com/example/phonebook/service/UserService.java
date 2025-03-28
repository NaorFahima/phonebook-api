package com.example.phonebook.service;

import com.example.phonebook.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserService extends GenericService<User, String> {
    Optional<User> getByPhoneNumber(String phoneNumber);
    Page<User> getByPhoneNumber(String phoneNumber, Pageable pageable);


}