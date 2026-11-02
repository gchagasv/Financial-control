package com.financial.control.service;

import com.financial.control.dtos.CategoryRequest;
import com.financial.control.dtos.CategoryResponse;
import com.financial.control.exceptions.CategoryAlreadyExistsException;
import com.financial.control.exceptions.InvalidCategoryException;
import com.financial.control.models.Category;
import com.financial.control.models.enums.TransactionType;
import com.financial.control.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryResponse createCategory(CategoryRequest dto) {
        if (dto.type() == TransactionType.INVESTMENT) {
            throw new InvalidCategoryException("A category must be EXPENSE or INCOME");
        }

        if (categoryRepository.existsByNameIgnoreCase(dto.name())) {
            throw new CategoryAlreadyExistsException("You cannot have two categories with the same name");
        }

        Category newCategory = new Category();
        newCategory.setName(dto.name());
        newCategory.setTransactionType(dto.type());

        Category saved = categoryRepository.save(newCategory);
        return new CategoryResponse(saved.getId(), saved.getName(),saved.getTransactionType());
    }

}

