package com.financial.control.service;

import com.financial.control.dtos.CategoryRequest;
import com.financial.control.dtos.CategoryResponse;
import com.financial.control.exceptions.CategoryAlreadyExistsException;
import com.financial.control.exceptions.CategoryNotFoundException;
import com.financial.control.exceptions.InvalidCategoryException;
import com.financial.control.models.Category;
import com.financial.control.models.enums.TransactionType;
import com.financial.control.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryResponse createCategory(CategoryRequest dto) {
        validateTransactionType(dto);

        if (categoryRepository.existsByNameIgnoreCase(dto.name())) {
            throw new CategoryAlreadyExistsException("You cannot have two categories with the same name");
        }

        Category newCategory = new Category();
        newCategory.setName(dto.name());
        newCategory.setTransactionType(dto.transactionType());

        return toResponse(categoryRepository.save(newCategory));
    }

    public CategoryResponse findById(Long id) {
        return toResponse(getOrThrow(id));
    }

    public CategoryResponse updateCategory(Long id, CategoryRequest dto) {
        Category category = getOrThrow(id);

        validateTransactionType(dto);

        if (categoryRepository.existsByNameIgnoreCaseAndIdNot(dto.name(), id)) {
            throw new CategoryAlreadyExistsException("You cannot have two categories with the same name");
        }

        category.setName(dto.name());
        category.setTransactionType(dto.transactionType());
        return toResponse(categoryRepository.save(category));
    }

    public void deleteCategory(Long id) {
        categoryRepository.delete(getOrThrow(id));
    }

    private Category getOrThrow(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with id " + id));
    }

    private void validateTransactionType(CategoryRequest dto) {
        if (dto.transactionType() == TransactionType.INVESTMENT) {
            throw new InvalidCategoryException("A category must be EXPENSE or INCOME");
        }
    }

    private CategoryResponse toResponse(Category c) {
        return new CategoryResponse(c.getId(), c.getName(), c.getTransactionType());
    }

    public List<CategoryResponse> findAll() {
        return categoryRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

}

