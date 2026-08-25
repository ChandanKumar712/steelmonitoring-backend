package com.chandan.steelmonitoring.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ProductDTO {

    private Long id;

    @NotBlank(message = "Product ID is required")
    private String productId;

    @NotBlank(message = "Machine Name is required")
    private String machineName;

    @NotBlank(message = "Shift is required")
    private String shift;

    @Positive(message = "Quantity must be greater than 0")
    private int quantity;

}