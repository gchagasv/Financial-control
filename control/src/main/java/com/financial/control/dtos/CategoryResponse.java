package com.financial.control.dtos;

import com.financial.control.models.enums.TransactionType;

public record CategoryResponse(
        Long id,
        String name,
        TransactionType type
) {
}
