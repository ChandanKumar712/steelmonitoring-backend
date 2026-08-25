package com.chandan.steelmonitoring.specification;

import com.chandan.steelmonitoring.entity.Product;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecification {

    public static Specification<Product> hasMachineName(String machineName) {

        return (root, query, cb) ->

                machineName == null
                        ? null
        // : cb.equal(root.get("machineName"), machineName);
        ///// LIKE Search (Contains Search)
                        : cb.like(
                cb.lower(root.get("machineName")),
                "%" + machineName.toLowerCase() + "%"
        );
    }


    public static Specification<Product> hasShift(String shift) {

        return (root, query, cb) ->

                shift == null
                        ? null
                     //   : cb.equal(root.get("shift"), shift);
                        : cb.like(
                cb.lower(root.get("shift")),
                "%" + shift.toLowerCase() + "%"
        );
    }

    public static Specification<Product> hasProductId(String productId) {

        return (root, query, cb) ->

                productId == null
                        ? null
        // : cb.equal(root.get("productId"), productId);
                        : cb.like(
                cb.lower(root.get("productId")),
                "%" + productId.toLowerCase() + "%"

        );
    }

    //////    Min/Max Quantity Filter
    public static Specification<Product> hasMinQuantity(Integer minQuantity) {

        return (root, query, cb) ->

                minQuantity == null
                        ? null
                        : cb.greaterThanOrEqualTo(
                        root.get("quantity"),
                        minQuantity
                );
    }

    public static Specification<Product> hasMaxQuantity(Integer maxQuantity) {

        return (root, query, cb) ->

                maxQuantity == null
                        ? null
                        : cb.lessThanOrEqualTo(
                        root.get("quantity"),
                        maxQuantity
                );
    }
}