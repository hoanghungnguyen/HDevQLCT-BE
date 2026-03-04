package com.duan.service;

import com.duan.dto.UserDto;
import com.duan.model.User;
import com.duan.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDto register(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        
        // Encode password before saving
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        
        User savedUser = userRepository.save(user);
        return mapToDto(savedUser);
    }

    public Optional<UserDto> findByUsername(String username) {
        return userRepository.findByUsername(username).map(this::mapToDto);
    }

    private UserDto mapToDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .build();
    }
}
