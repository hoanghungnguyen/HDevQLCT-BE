package com.duan.controller;

import com.duan.dto.CategoryDto;
import com.duan.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import com.duan.security.CustomUserDetails;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(
            @RequestBody CategoryDto categoryDto,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        // Set the userId from the secure token before passing to service
        categoryDto.setUserId(userDetails.getUser().getId());
        return ResponseEntity.ok(categoryService.createCategory(categoryDto));
    }

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getMyCategories(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(categoryService.getCategoriesByUser(userDetails.getUser().getId()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(
            @PathVariable Integer id,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            categoryService.deleteCategory(id, userDetails.getUser().getId());
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
