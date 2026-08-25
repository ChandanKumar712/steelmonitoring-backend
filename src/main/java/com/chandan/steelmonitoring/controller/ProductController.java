package com.chandan.steelmonitoring.controller;

import com.chandan.steelmonitoring.dto.ApiResponseDTO;
import com.chandan.steelmonitoring.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import jakarta.validation.Valid;
import com.chandan.steelmonitoring.dto.ProductDTO;
////    For Swagger
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

//// ADD Actual Role Restrictions ==> for role based access control
import org.springframework.security.access.prepost.PreAuthorize;

//////     For Pagination and Sorting
// import org.springframework.data.domain.page;

@Tag(
        name = "Product Management",
        description = "APIs for managing steel production products"
)
@RestController
@RequestMapping("/products")
public class ProductController {


    //////   Constructor Injection
    private final ProductService service;
    public ProductController(ProductService service) {
        this.service = service;
    }

    @ApiResponses(value = {

            @ApiResponse(responseCode = "201",
                    description = "Product created successfully"),

            @ApiResponse(responseCode = "400",
                    description = "Invalid product details")

    })
    @Operation(summary = "Add a new product")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")   // New line for Role based access.
    @PostMapping
    public ResponseEntity<ApiResponseDTO<ProductDTO>> addProduct(
            @Valid @RequestBody ProductDTO dto) {

        ProductDTO savedProduct = service.saveProduct(dto);


        ////    Custom API Response
        ApiResponseDTO<ProductDTO> response =
                new ApiResponseDTO<>(
                        true,
                        "Product created successfully",
                        savedProduct
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @ApiResponses(value = {

            @ApiResponse(responseCode = "200",
                    description = "Products fetched successfully")

    })
    @Operation(summary = "Get all products")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'OPERATOR')")   // ← For role based
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<ProductDTO>>> getAllProducts() {

        List<ProductDTO> products = service.getAllProducts();

        ApiResponseDTO<List<ProductDTO>> response =
                new ApiResponseDTO<>(
                        true,
                        "Products fetched successfully",
                        products
                );

        return ResponseEntity.ok(response);
    }

    @ApiResponses(value = {

            @ApiResponse(responseCode = "200",
                    description = "Product fetched successfully"),

            @ApiResponse(responseCode = "404",
                    description = "Product not found")

    })
    @Operation(summary = "Get product by ID")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'OPERATOR')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<ProductDTO>> getProductById(
            @PathVariable Long id) {

        ProductDTO product = service.getProductById(id);

        ApiResponseDTO<ProductDTO> response =
                new ApiResponseDTO<>(
                        true,
                        "Product fetched successfully",
                        product
                );

        return ResponseEntity.ok(response);
    }


    @ApiResponses(value = {

            @ApiResponse(responseCode = "200",
                    description = "Product updated successfully"),

            @ApiResponse(responseCode = "404",
                    description = "Product not found"),

            @ApiResponse(responseCode = "400",
                    description = "Invalid product details")

    })
    @Operation(summary = "Update product by ID")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<ProductDTO>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductDTO dto){

        ProductDTO updatedProduct = service.updateProduct(id, dto);

        ApiResponseDTO<ProductDTO> response =
                new ApiResponseDTO<>(
                        true,
                        "Product updated successfully",
                        updatedProduct
                );

        return ResponseEntity.ok(response);
    }


    @ApiResponses(value = {

            @ApiResponse(responseCode = "200",
                    description = "Product deleted successfully"),

            @ApiResponse(responseCode = "404",
                    description = "Product not found")

    })
    @Operation(summary = "Delete product by ID")
    @PreAuthorize("hasRole('ADMIN')")   ////    Only admin can delete product
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> deleteProduct(
            @PathVariable Long id) {

        service.deleteProduct(id);

        ApiResponseDTO<Void> response =
                new ApiResponseDTO<>(
                        true,
                        "Product deleted successfully",
                        null
                );

        return ResponseEntity.ok(response);
    }


    ////   Search by Machine
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'OPERATOR')")
    @GetMapping("/machine/{machineName}")
    public List<ProductDTO> getByMachine(
            @PathVariable String machineName) {

        return service.getByMachine(machineName);
    }


    //// Get by Shift For DTO
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'OPERATOR')")
    @GetMapping("/shift/{shift}")
    public List<ProductDTO> getByShift(
            @PathVariable String shift){

        return service.getByShift(shift);

    }

    /////   For DTO get by product ID
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'OPERATOR')")
    @GetMapping("/product/{productId}")
    public List<ProductDTO> getByProductId(
            @PathVariable String productId){

        return service.getByProductId(productId);

    }


    //////   Method For Pagination and Sorting
    @ApiResponses(value = {

            @ApiResponse(responseCode = "200",
                    description = "Products fetched successfully")

    })
    @Operation(summary = "Get products with pagination and sorting")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'OPERATOR')")
    @GetMapping("/page")
    public ResponseEntity<ApiResponseDTO<Page<ProductDTO>>> getProducts(

            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction

    ) {

        Page<ProductDTO> products =
                service.getProductsWithPagination(
                        page,
                        size,
                        sortBy,
                        direction);

        ApiResponseDTO<Page<ProductDTO>> response =
                new ApiResponseDTO<>(

                        true,
                        "Products fetched successfully",
                        products
                );

        return ResponseEntity.ok(response);

    }


    //////    Spring Data JPA Specification'
    @ApiResponses(value = {

            @ApiResponse(
                    responseCode = "200",
                    description = "Products searched successfully")

    })
    @Operation(summary = "Search products")
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR', 'OPERATOR')")
    @GetMapping("/search")
    public ResponseEntity<ApiResponseDTO<List<ProductDTO>>> searchProducts(

            @RequestParam(required = false)
            String machineName,

            @RequestParam(required = false)
            String shift,

            @RequestParam(required = false)
            String productId,

            @RequestParam(required = false)
            Integer minQuantity,

            @RequestParam(required = false)
            Integer maxQuantity

    ) {

        List<ProductDTO> products =
                service.searchProducts(
                        machineName,
                        shift,
                        productId,
                        minQuantity,
                        maxQuantity
                );

        ApiResponseDTO<List<ProductDTO>> response =
                new ApiResponseDTO<>(
                        true,
                        "Products fetched successfully",
                        products
                );

        return ResponseEntity.ok(response);
    }

}