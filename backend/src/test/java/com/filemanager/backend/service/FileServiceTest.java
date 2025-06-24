package com.filemanager.backend.service;

import com.filemanager.backend.entity.File;
import com.filemanager.backend.entity.File.Visibility;
import com.filemanager.backend.repository.FileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FileServiceTest {

    private FileRepository fileRepository;
    private FileService fileService;

    @BeforeEach
    void setUp() {
        fileRepository = mock(FileRepository.class);
        fileService = new FileService(fileRepository);
    }

    @Test
    void testGetFileById_FileExists_ReturnsFile() {
        File file = new File();
        file.setId(1L);
        when(fileRepository.findById(1L)).thenReturn(Optional.of(file));

        Optional<File> result = fileService.getFileById(1L);

        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        verify(fileRepository, times(1)).findById(1L);
    }

    @Test
    void testGetAllFiles_ReturnsList() {
        List<File> files = List.of(new File(), new File());
        when(fileRepository.findAll()).thenReturn(files);

        List<File> result = fileService.getAllFiles();

        assertEquals(2, result.size());
        verify(fileRepository, times(1)).findAll();
    }

    @Test
    void testSaveFile_SavesSuccessfully() {
        File file = new File();
        file.setName("example.txt");

        when(fileRepository.save(file)).thenReturn(file);

        File savedFile = fileService.saveFile(file);

        assertEquals("example.txt", savedFile.getName());
        verify(fileRepository, times(1)).save(file);
    }

    @Test
    void testUpdateFile_UpdatesExistingFile() {
        Long fileId = 1L;
        File existingFile = new File();
        existingFile.setId(fileId);
        existingFile.setName("old.txt");

        File updatedFile = new File();
        updatedFile.setName("new.txt");
        updatedFile.setPath("/new");
        updatedFile.setSize(1024L);
        updatedFile.setVisibility(Visibility.PUBLIC);

        when(fileRepository.findById(fileId)).thenReturn(Optional.of(existingFile));
        when(fileRepository.save(any(File.class))).thenReturn(existingFile);

        File result = fileService.updateFile(fileId, updatedFile);

        assertEquals("new.txt", result.getName());
        assertEquals("/new", result.getPath());
        assertEquals(1024L, result.getSize());
        assertEquals(Visibility.PUBLIC, result.getVisibility());
        verify(fileRepository, times(1)).save(existingFile);
    }

    @Test
    void testDeleteFile_DeletesSuccessfully() {
        Long fileId = 1L;
        when(fileRepository.existsById(fileId)).thenReturn(true);
        doNothing().when(fileRepository).deleteById(fileId);

        fileService.deleteFile(fileId);

        verify(fileRepository, times(1)).deleteById(fileId);
    }
}
