package com.duan.controller;

import com.duan.dto.CategoryDto;
import com.duan.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@RequestBody CategoryDto categoryDto) {
        return ResponseEntity.ok(categoryService.createCategory(categoryDto));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CategoryDto>> getCategoriesByUser(@PathVariable Integer userId) {
        return ResponseEntity.ok(categoryService.getCategoriesByUser(userId));
    }
}
