package com.chandan.steelmonitoring.entity;

import jakarta.persistence.*;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@Entity
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //   To ensure Validation
  //  @NotBlank(message = "Product ID is required")
 //   private String productId;

    @NotBlank(message = "Machine Name is required")
    private String machineName;

    @NotBlank(message = "Shift is required")
    private String shift;

    @Positive(message = "Quantity must be greater than 0")
    private int quantity;

    @Column(unique = true)
    @NotBlank(message = "Product ID is required")
    private String productId;
}