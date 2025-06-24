package com.filemanager.backend.repository;

import com.filemanager.backend.entity.Folder;
import com.filemanager.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for working with Folder entities.
 */
@Repository
public interface FolderRepository extends JpaRepository<Folder, Long> {

    Optional<Folder> findByName(String name);

    boolean existsByName(String name);

    // Retrieve all folders owned by the given user
    List<Folder> findByOwner(User owner);

    // Retrieve subfolders by their parent folder
    List<Folder> findByParentFolder(Folder parentFolder);
}
