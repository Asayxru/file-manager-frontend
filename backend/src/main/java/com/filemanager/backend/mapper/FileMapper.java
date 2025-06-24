package com.filemanager.backend.mapper;

import com.filemanager.backend.dto.FileResponseDTO;
import com.filemanager.backend.entity.File;
import org.springframework.stereotype.Component;

/**
 * Mapper to convert File entity into a response-safe DTO.
 */
@Component
public class FileMapper {

    
    public FileResponseDTO toDTO(File file) {
        FileResponseDTO dto = new FileResponseDTO();
        dto.setId(file.getId());
        dto.setName(file.getName());
        dto.setPath(file.getPath());
        dto.setSize(file.getSize());
        dto.setVisibility(file.getVisibility().name());
        dto.setCreatedAt(file.getCreatedAt());
        dto.setUpdatedAt(file.getUpdatedAt());

        if (file.getOwner() != null) {
            dto.setOwnerUsername(file.getOwner().getUsername());
        }

        if (file.getFolder() != null) {
            dto.setFolderId(file.getFolder().getId());
            dto.setFolderName(file.getFolder().getName()); // нове поле
        }

        return dto;
    }
}
