package com.chandan.steelmonitoring.service;

import com.chandan.steelmonitoring.dto.ProductDTO;
import com.chandan.steelmonitoring.entity.Product;
import com.chandan.steelmonitoring.exception.ProductNotFoundException;
import com.chandan.steelmonitoring.repository.ProductRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/////   Logging (SLF4J + Logback)
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/////      For Pagination and Sorting

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

//////   For Spring Data JPA Specification
import org.springframework.data.jpa.domain.Specification;
import com.chandan.steelmonitoring.specification.ProductSpecification;

@Service
public class ProductService {

    ///////    Logging (SLF4J + Logback)
    private static final Logger logger =
            LoggerFactory.getLogger(ProductService.class);

    //////    Constructor Injection
    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }


    /////       For DTO With Logging (SLF4J + Logback)
    public ProductDTO saveProduct(ProductDTO dto){

        logger.info("Creating product with Product ID : {}", dto.getProductId());

        Product product = dtoToEntity(dto);

        Product savedProduct = repository.save(product);

        logger.info("Product created successfully with ID : {}",
                savedProduct.getId());

        return entityToDto(savedProduct);
    }


    public List<ProductDTO> getAllProducts() {

        logger.info("Fetching all products");

        List<Product> products = repository.findAll();

        logger.info("Total products fetched : {}", products.size());

        return products.stream()
                .map(this::entityToDto)
                .toList();

    }


    ////    For DTO
public ProductDTO getProductById(Long id) {
    logger.info("Fetching product with id {}", id);

    Product product = repository.findById(id)
//            .orElseThrow(() -> new RuntimeException("Product Not Found"));
            .orElseThrow(() ->
                    new ProductNotFoundException(
                            "Product not found with id : " + id));
    logger.info("Product fetched successfully");
    return entityToDto(product);
}

/////   For DTO
    public ProductDTO updateProduct(Long id, ProductDTO updatedProduct) {
        logger.info("Updating product with id {}", id);

        Product existingProduct = repository.findById(id)
                //  .orElseThrow(() -> new RuntimeException("Product Not Found"));
        .orElseThrow(() ->
                new ProductNotFoundException(
                        "Product not found with id : " + id));

        existingProduct.setProductId(updatedProduct.getProductId());
        existingProduct.setMachineName(updatedProduct.getMachineName());
        existingProduct.setShift(updatedProduct.getShift());
        existingProduct.setQuantity(updatedProduct.getQuantity());

        Product savedProduct = repository.save(existingProduct);
        logger.info("Product updated successfully with id {}", id);
        return entityToDto(savedProduct);
    }


    public void deleteProduct(Long id) {
        logger.info("Deleting product with id {}", id);
        Product product = repository.findById(id)
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "Product not found with id : " + id));

        repository.delete(product);
        logger.info("Product deleted successfully");

    }


////    For DTO
public List<ProductDTO> getByMachine(String machineName){

    logger.info("Searching products by machine : {}", machineName);

    return repository.findByMachineName(machineName)
            .stream()
            .map(this::entityToDto)
            .toList();
}


    ////     For DTO
    public List<ProductDTO> getByShift(String shift){

        logger.info("Searching products by shift : {}", shift);

        return repository.findByShift(shift)
                .stream()
                .map(this::entityToDto)
                .toList();
    }


   //// Convert Product ID Search into DTO

    public List<ProductDTO> getByProductId(String productId){

        logger.info("Searching product by Product ID : {}", productId);

        return repository.findByProductId(productId)
                .stream()
                .map(this::entityToDto)
                .toList();
    }





    /////    For DTO
    private Product dtoToEntity(ProductDTO dto){

        Product product = new Product();

        product.setProductId(dto.getProductId());

        product.setMachineName(dto.getMachineName());

        product.setShift(dto.getShift());

        product.setQuantity(dto.getQuantity());

        return product;

    }

    private ProductDTO entityToDto(Product product){

        ProductDTO dto = new ProductDTO();

        dto.setId(product.getId());  //// For handle edit/delete permission

        dto.setProductId(product.getProductId());

        dto.setMachineName(product.getMachineName());

        dto.setShift(product.getShift());

        dto.setQuantity(product.getQuantity());

        return dto;

    }

    public Page<ProductDTO> getProductsWithPagination(
            int page,
            int size,
            String sortBy,
            String direction) {

        logger.info("Fetching products page {}, size {}, sortBy {}, direction {}",
                page, size, sortBy, direction);

        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Product> productPage = repository.findAll(pageable);

        logger.info("Fetched {} products", productPage.getNumberOfElements());

        return productPage.map(this::entityToDto);
    }

    //////    Spring Data JPA Specification
    public List<ProductDTO> searchProducts(
            String machineName,
            String shift,
            String productId,
            Integer minQuantity,
            Integer maxQuantity){

        logger.info(
                "Searching products. Machine: {}, Shift: {}, ProductId: {}",
                machineName,
                shift,
                productId);

        Specification<Product> specification =
                Specification.allOf(
                        ProductSpecification.hasMachineName(machineName),
                        ProductSpecification.hasShift(shift),
                        ProductSpecification.hasProductId(productId),
                        ProductSpecification.hasMinQuantity(minQuantity),
                        ProductSpecification.hasMaxQuantity(maxQuantity)
                );

        List<Product> products = repository.findAll(specification);

        logger.info("Found {} products", products.size());

        return products.stream()
                .map(this::entityToDto)
                .toList();
    }

}