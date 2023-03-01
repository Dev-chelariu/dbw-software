package com.example.software.data.service.implementation;

import com.example.software.data.entity.User;
import com.example.software.data.entity.enums.Role;
import com.example.software.data.repository.UserRepository;
import com.example.software.security.SecurityConfiguration;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final SecurityConfiguration securityConfiguration;


    public User registerAll(User user, InputStream profilePicture, String fileName) {
        // Save the uploaded file to a directory on the server
        String uploadDir = "uploads/profile-pictures/";
        String filePath = uploadDir + fileName;
        try {
            File file = new File(filePath);
            FileUtils.copyInputStreamToFile(profilePicture, file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save profile picture: " + e.getMessage());
        }

        // Set the profile picture filename on the user entity
        user.setProfilePicture(fileName.getBytes());

        // Save the user entity to the database
        return repository.save(user);
    }

    public Optional<User> get(Long id) {
        return repository.findById(id);
    }

    public User update(User entity) {
        return repository.save(entity);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
    public User register(User user, Set<Role> roles) {
        user.setRoles(roles);
        user.setHashedPassword(securityConfiguration.passwordEncoder ().encode (user.getHashedPassword ()));
        return repository.save(user);
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
