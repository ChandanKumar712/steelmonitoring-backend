package com.chandan.steelmonitoring.service;

import com.chandan.steelmonitoring.dto.ProductDTO;
import com.chandan.steelmonitoring.entity.Product;
import com.chandan.steelmonitoring.exception.ProductNotFoundException;
import com.chandan.steelmonitoring.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    void shouldReturnProduct_whenIdExists() {

        // ARRANGE
        Product fakeProduct = new Product();
        fakeProduct.setId(1L);
        fakeProduct.setProductId("ST105");
        fakeProduct.setMachineName("Line-B");
        fakeProduct.setShift("Night");
        fakeProduct.setQuantity(700);

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(fakeProduct));

        // ACT
        ProductDTO result = productService.getProductById(1L);

        // ASSERT
        assertEquals("ST105", result.getProductId());
        assertEquals("Line-B", result.getMachineName());
        assertEquals(700, result.getQuantity());
    }

    @Test
    void shouldThrowException_whenIdDoesNotExist() {

        // ARRANGE
        when(productRepository.findById(99L))
                .thenReturn(Optional.empty());

        // ACT + ASSERT
        assertThrows(ProductNotFoundException.class, () -> {
            productService.getProductById(99L);
        });
    }
}