package com.example.phonebook.repository;

import com.example.phonebook.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    Page<User> findByPhoneNumber(String phoneNumber, Pageable pageable);
    Optional<User> findByPhoneNumber(String phoneNumber);

}