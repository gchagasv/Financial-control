package com.financial.control.dtos;

import com.financial.control.models.enums.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CategoryRequest(
        @NotBlank(message = "is required") String name,
        @NotNull(message = "is required") TransactionType transactionType
        ) {
}
