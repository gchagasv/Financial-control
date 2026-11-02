package com.financial.control.dtos;

import com.financial.control.models.enums.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CategoryRequest(
        @NotBlank String name,
        @NotNull TransactionType type
        ) {
}
