package com.example.software.data.service;

import com.example.software.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    User findByUsername(String username);
}
