package com.filemanager.backend.service;

import com.filemanager.backend.dto.UserDTO;
import com.filemanager.backend.entity.User;
import com.filemanager.backend.mapper.UserMapper;
import com.filemanager.backend.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<UserDTO> getUserById(Long id) {
        logger.info("Fetching user with ID: {}", id);
        return userRepository.findById(id).map(UserMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public User getUserEntityById(Long id) {
        logger.info("Fetching User entity with ID: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + id));
    }

    public List<UserDTO> getAllUsers() {
        logger.info("Fetching all users");
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<User> getByUsername(String username) {
        logger.info("Fetching user by username: {}", username);
        return userRepository.findByUsername(username);
    }

    public User getCurrentAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        logger.info("Current authenticated username: {}", username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    @Transactional
    public UserDTO saveUser(UserDTO userDTO) {
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }

        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
        }

        logger.info("Saving new user: {}", userDTO.getUsername());

        User user = UserMapper.toEntity(userDTO);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));

        return UserMapper.toDTO(userRepository.save(user));
    }

    @Transactional
    public UserDTO updateUser(Long id, UserDTO updatedDTO) {
        logger.info("Updating user with ID: {}", id);

        return userRepository.findById(id)
                .map(existing -> {
                    if (!existing.getUsername().equals(updatedDTO.getUsername())
                            && userRepository.existsByUsername(updatedDTO.getUsername())) {
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
                    }

                    if (!existing.getEmail().equals(updatedDTO.getEmail())
                            && userRepository.existsByEmail(updatedDTO.getEmail())) {
                        throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already exists");
                    }

                    existing.setUsername(updatedDTO.getUsername());
                    existing.setEmail(updatedDTO.getEmail());

                    if (updatedDTO.getPassword() != null && !updatedDTO.getPassword().isBlank()) {
                        existing.setPassword(passwordEncoder.encode(updatedDTO.getPassword()));
                    }

                    existing.setRole(updatedDTO.getRole());
                    return UserMapper.toDTO(userRepository.save(existing));
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    /**
     * Updates password of a user securely.
     */
    @Transactional
    public void updatePassword(Long id, String newPassword) {
        logger.info("Updating password for user ID: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            logger.warn("User with ID: {} not found", id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }

        logger.info("Deleting user with ID: {}", id);
        userRepository.deleteById(id);
    }
}
