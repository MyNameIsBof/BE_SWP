package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UpdateUserRepository extends JpaRepository<User, Long> {
    Optional<User> findAccountById(Long id);
}
