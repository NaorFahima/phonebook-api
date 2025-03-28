package com.example.phonebook.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

public interface GenericService<T, ID> {
    Page<T> getAll(Pageable pageable);
    Optional<T> getById(ID id);
    T create(T entity);
    Optional<T> update(ID id, T entity);
    boolean delete(ID id);
}