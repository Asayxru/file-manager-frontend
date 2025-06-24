package com.filemanager.backend.repository;

import com.filemanager.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for working with User entities.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    // Check if a user exists by username
    boolean existsByUsername(String username);

    // Check if a user exists by email
    boolean existsByEmail(String email);
}
