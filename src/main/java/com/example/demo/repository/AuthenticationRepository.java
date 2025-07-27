package com.example.demo.repository;

import com.example.demo.entity.User;
import com.example.demo.enums.BloodType;
import com.example.demo.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuthenticationRepository extends JpaRepository<User, Long> {
    User findUserByEmail(String email);
    List<User> findByRoleNot(Role role);
    // Tìm user đã đăng nhập, đủ điều kiện, có nhóm máu phù hợp
    List<User> findByBloodType(BloodType bloodType);
}

