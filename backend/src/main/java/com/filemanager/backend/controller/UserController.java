package com.filemanager.backend.controller;

import com.filemanager.backend.dto.UpdatePasswordRequest;
import com.filemanager.backend.dto.UserDTO;
import com.filemanager.backend.entity.User;
import com.filemanager.backend.mapper.UserMapper;
import com.filemanager.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Returns the profile information of the currently authenticated user.
     */
    @GetMapping("/me")
    public ResponseEntity<UserDTO> getCurrentUser() {
        User current = userService.getCurrentAuthenticatedUser();
        return ResponseEntity.ok(UserMapper.toDTO(current));
    }

    /**
     * Updates only the password of the currently authenticated user.
     */
    @PutMapping("/me")
    public ResponseEntity<?> updatePassword(@RequestBody @Valid UpdatePasswordRequest request) {
        User current = userService.getCurrentAuthenticatedUser();
        userService.updatePassword(current.getId(), request.getPassword());
        return ResponseEntity.ok("Password updated successfully");
    }

    /**
     * Deletes the currently authenticated user account.
     */
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteCurrentUser() {
        User current = userService.getCurrentAuthenticatedUser();
        userService.deleteUser(current.getId());
        return ResponseEntity.noContent().build();
    }
}
