package com.chandan.steelmonitoring.repository;

import com.chandan.steelmonitoring.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductRepository
        extends JpaRepository<Product, Long>,
//////   Implement Spring Data JPA Specification
            JpaSpecificationExecutor<Product> {

    List<Product> findByMachineName(String machineName);

    List<Product> findByShift(String shift);

    List<Product> findByProductId(String productId);

    List<Product> findByMachineNameAndShift(
            String machineName,
            String shift);

}