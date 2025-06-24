package com.filemanager.backend.service;

import com.filemanager.backend.entity.File;
import com.filemanager.backend.repository.FileRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
public class FileService {

    private static final Logger logger = LoggerFactory.getLogger(FileService.class);
    private final FileRepository fileRepository;

    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    public Optional<File> getFileById(Long id) {
        logger.info("Fetching file with ID: {}", id);
        return fileRepository.findById(id);
    }

    public Optional<File> getFileByName(String name) {
        logger.info("Fetching file by name: {}", name);
        return fileRepository.findByName(name);
    }

    public List<File> getAllFiles() {
        logger.info("Fetching all files");
        return fileRepository.findAll();
    }

    public List<File> getFilesByOwner(String username) {
        logger.info("Fetching files owned by user: {}", username);
        return fileRepository.findAllByOwner_Username(username);
    }

    public List<File> searchFilesByName(String query) {
        return fileRepository.findByNameContainingIgnoreCase(query);
    }

public List<File> searchFilesByNameAndOwner(String query, String username) {
        return fileRepository.findByNameContainingIgnoreCaseAndOwner_Username(query, username);
    }


    @Transactional
    public File saveFile(File file) {
        logger.info("Saving new file: {}", file.getName());
        return fileRepository.save(file);
    }
    

    @Transactional
    public File updateFile(Long id, File updatedFile) {
        return fileRepository.findById(id)
                .map(file -> {
                    file.setName(updatedFile.getName());
                    file.setPath(updatedFile.getPath());
                    file.setSize(updatedFile.getSize());
                    file.setVisibility(updatedFile.getVisibility());
                    logger.info("Updating file with ID: {}", id);
                    return fileRepository.save(file);
                })
                .orElseThrow(() -> new EntityNotFoundException("File not found"));
    }

    @Transactional
    public void deleteFile(Long id) {
        File file = fileRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("File not found"));

        // Удаление с диска
        try {
            Path path = Paths.get(file.getPath());
            if (Files.exists(path)) {
                Files.delete(path);
                logger.info("Deleted file from FS: {}", file.getPath());
            }
        } catch (IOException e) {
            logger.error("Ошибка при удалении файла из файловой системы", e);
        }

        logger.info("Deleting file with ID: {}", id);
        fileRepository.deleteById(id);
    }
}
