package com.sdatfinals.backend.user;

import com.sdatfinals.backend.auth.RegisterRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Set;

@Service
public class UserService {

    private static final Set<String> VALID_ROLES = Set.of("user", "admin");

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "An account with that email already exists");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole("user");
        return userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "no user found with email: " + email));
    }

    public void recordLogin(User user) {
        user.setLastLoginAt(Instant.now());
        userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(Long id) {
        User user = getById(id);
        if ("admin".equals(user.getRole())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't delete an admin user");
        }
        userRepository.deleteById(id);
    }

    public User updateRole(Long id, String role) {
        if (role == null || !VALID_ROLES.contains(role)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "role must be user or admin");
        }
        User user = getById(id);
        user.setRole(role);
        return userRepository.save(user);
    }

    private User getById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "no user found with id: " + id));
    }
}
