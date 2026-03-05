package com.duan.service;

import com.duan.dto.UserDto;
import com.duan.model.User;
import com.duan.model.Category;
import com.duan.model.TransactionType;
import com.duan.repository.UserRepository;
import com.duan.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.List;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
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

        // Seed default categories for this newly registered user
        List<Category> defaultCategories = Arrays.asList(
            Category.builder().user(savedUser).name("Lương/Thu nhập").type(TransactionType.income).icon("wallet").build(),
            Category.builder().user(savedUser).name("Tiền ăn uống").type(TransactionType.expense).icon("coffee").build(),
            Category.builder().user(savedUser).name("Tiền nhà/Điện nước").type(TransactionType.expense).icon("home").build(),
            Category.builder().user(savedUser).name("Mua sắm").type(TransactionType.expense).icon("shopping-bag").build(),
            Category.builder().user(savedUser).name("Di chuyển").type(TransactionType.expense).icon("car").build(),
            Category.builder().user(savedUser).name("Giải trí").type(TransactionType.expense).icon("film").build(),
            Category.builder().user(savedUser).name("Khác").type(TransactionType.expense).icon("more-horizontal").build()
        );
        categoryRepository.saveAll(defaultCategories);

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
