package com.financial.control.dtos;


import com.financial.control.models.enums.AssetType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AssetRequest(
        @NotBlank(message = "is required") String ticker,
        @NotNull(message = "is required") AssetType assetType
) {
}
