package com.example.software.data.repository;

import com.example.software.data.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {

    List<Product> findByNameContainingIgnoreCase(String name);

    Product findByName(String name);

    @Query("SELECT p FROM Product p WHERE p.stockCount < 10")
    List<Product> findLowStockProducts();
}
