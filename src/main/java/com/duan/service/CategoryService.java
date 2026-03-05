package com.duan.service;

import com.duan.dto.CategoryDto;
import com.duan.model.Category;
import com.duan.model.User;
import com.duan.repository.CategoryRepository;
import com.duan.repository.TransactionRepository;
import com.duan.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public CategoryDto createCategory(CategoryDto categoryDto) {
        User user = userRepository.findById(categoryDto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Category category = Category.builder()
                .user(user)
                .name(categoryDto.getName())
                .type(categoryDto.getType())
                .icon(categoryDto.getIcon())
                .build();
                
        Category savedCategory = categoryRepository.save(category);
        categoryDto.setId(savedCategory.getId());
        return categoryDto;
    }

    public List<CategoryDto> getCategoriesByUser(Integer userId) {
        return categoryRepository.findByUserId(userId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public void deleteCategory(Integer categoryId, Integer userId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (!category.getUser().getId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền xóa danh mục này");
        }

        if (transactionRepository.existsByCategoryId(categoryId)) {
            throw new RuntimeException("Không thể xóa danh mục đang có giao dịch!");
        }

        categoryRepository.delete(category);
    }

    private CategoryDto mapToDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .userId(category.getUser().getId())
                .name(category.getName())
                .type(category.getType())
                .icon(category.getIcon())
                .build();
    }
}
