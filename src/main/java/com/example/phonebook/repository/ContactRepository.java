package com.example.phonebook.repository;

import com.example.phonebook.entity.Contact;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface ContactRepository extends MongoRepository<Contact, String> {
    Page<Contact> findByUserId(String userId, Pageable pageable);
    @Query("{'userId': ?0, $or: [ {'firstName': {$regex: ?1, $options: 'i'}}, {'lastName': {$regex: ?1, $options: 'i'}}, {'phoneNumber': {$regex: ?1, $options: 'i'}} ]}")
    Page<Contact> searchByUserIdAndQuery(String userId, String query, Pageable pageable);
}
