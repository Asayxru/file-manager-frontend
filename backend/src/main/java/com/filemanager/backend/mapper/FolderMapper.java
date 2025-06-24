package com.filemanager.backend.mapper;

import com.filemanager.backend.dto.FileResponseDTO;
import com.filemanager.backend.dto.FolderResponseDTO;
import com.filemanager.backend.entity.Folder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class FolderMapper {

    private final FileMapper fileMapper;

    public FolderMapper(FileMapper fileMapper) {
        this.fileMapper = fileMapper;
    }

 
    public FolderResponseDTO toDTO(Folder folder) {
        FolderResponseDTO dto = new FolderResponseDTO();
        dto.setId(folder.getId());
        dto.setName(folder.getName());
        dto.setOwnerId(folder.getOwner() != null ? folder.getOwner().getId() : null);
        dto.setParentFolderId(folder.getParentFolder() != null ? folder.getParentFolder().getId() : null);
        dto.setCreatedAt(folder.getCreatedAt());
        dto.setUpdatedAt(folder.getUpdatedAt());
        return dto;
    }

   
    public FolderResponseDTO toDTOWithFiles(Folder folder) {
        FolderResponseDTO dto = toDTO(folder);

        
        List<FileResponseDTO> files = folder.getFiles() != null
                ? folder.getFiles().stream()
                    .map(fileMapper::toDTO)
                    .collect(Collectors.toList())
                : List.of();
        dto.setFiles(files);

        
        List<FolderResponseDTO> subfolders = folder.getSubfolders() != null
                ? folder.getSubfolders().stream()
                    .map(this::toDTOWithFiles)
                    .collect(Collectors.toList())
                : List.of();
        dto.setSubfolders(subfolders);

        return dto;
    }
}
