package com.filemanager.backend.controller;

import com.filemanager.backend.dto.JwtResponse;
import com.filemanager.backend.dto.LoginRequest;
import com.filemanager.backend.dto.UserDTO;
import com.filemanager.backend.dto.UserRegisterDTO;
import com.filemanager.backend.entity.User;
import com.filemanager.backend.security.JwtUtils;
import com.filemanager.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public AuthController(UserService userService, AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@RequestBody @Valid UserRegisterDTO dto) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername(dto.getUsername());
        userDTO.setEmail(dto.getEmail());
        userDTO.setPassword(dto.getPassword());
        userDTO.setRole(User.Role.USER);

        UserDTO saved = userService.saveUser(userDTO);
        return ResponseEntity.ok(saved);
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(), loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // ⬇️ ВАЖЛИВО: отримати UserDetails для генерації токена
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwt = jwtUtils.generateToken(userDetails);

        return ResponseEntity.ok(new JwtResponse(jwt));
    }
}
