package com.example.software.data.service.implementation;

import com.example.software.data.entity.User;
import com.example.software.data.repository.UserRepository;
import com.example.software.security.SecurityConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final SecurityConfiguration securityConfiguration;


    public Optional<User> get(Long id) {
        return repository.findById(id);
    }

    public User update(User entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
    public User register(User user) {
        return repository.save(new User(user.getUsername (), user.getName (),
                securityConfiguration.passwordEncoder ().encode (user.getHashedPassword ())));
    }

    public User delete(User user) {
        repository.delete(user);
        return null;
    }

    public List<User> findAll() {
        return repository.findAll ();
    }

    public int count() {
        return (int) repository.count();
    }
}
