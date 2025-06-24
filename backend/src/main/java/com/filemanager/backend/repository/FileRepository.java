package com.filemanager.backend.repository;

import com.filemanager.backend.entity.File;
import com.filemanager.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for working with File entities.
 */
public interface FileRepository extends JpaRepository<File, Long> {
    List<File> findByOwner(User owner);
    List<File> findAllByOwner_Username(String username);

    List<File> findByNameContainingIgnoreCase(String name);
    List<File> findByNameContainingIgnoreCaseAndOwner_Username(String name, String username);


    // Find file by its name (used in legacy preview endpoint)
    Optional<File> findByName(String name);
}
