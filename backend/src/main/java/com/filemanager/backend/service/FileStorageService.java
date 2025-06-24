package com.filemanager.backend.service;

import com.filemanager.backend.entity.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;

@Service
public class FileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);
    private final Path rootLocation = Paths.get("uploads");

    public FileStorageService() throws IOException {
        if (!Files.exists(rootLocation)) {
            Files.createDirectories(rootLocation);
        }
    }

    public File store(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            logger.warn("Попытка загрузить пустой файл: {}", file.getOriginalFilename());
            // Можно продолжить выполнение — загрузка пустых файлов разрешена
        }

        Path destinationFile = rootLocation.resolve(Path.of(file.getOriginalFilename()))
                .normalize().toAbsolutePath();

        if (!destinationFile.getParent().equals(rootLocation.toAbsolutePath())) {
            throw new IOException("Недопустимое имя файла");
        }

        Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);

        File fileEntity = new File();
        fileEntity.setName(file.getOriginalFilename());
        fileEntity.setPath(destinationFile.toString());
        fileEntity.setSize(file.getSize());
        fileEntity.setVisibility(File.Visibility.PRIVATE);
        fileEntity.setCreatedAt(LocalDateTime.now());
        fileEntity.setUpdatedAt(LocalDateTime.now());

        return fileEntity;
    }

    public boolean delete(String filename) throws IOException {
        Path file = rootLocation.resolve(filename);
        return Files.deleteIfExists(file);
    }

    public String readAsText(String filename) throws IOException {
        Path file = rootLocation.resolve(filename);
        return Files.readString(file);
    }
}
 