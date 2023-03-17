package com.example.software.data.service;

import com.example.software.data.entity.Product;
import com.example.software.data.entity.dto.ProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IProduct {

    Integer getPrice(Long productId);

    List<ProductDTO> findAllProducts();

    List<Product> findAll();

    ProductDTO addProduct(ProductDTO productDto);

    List<Product> getLowQuantityProducts();

    ProductDTO getProductById(Long id);

    ProductDTO update(ProductDTO productDto);

    ProductDTO delete(ProductDTO productDTO);

    Page<Product> list(Pageable pageable);

    List<ProductDTO> findByNameContainingIgnoreCase(String name);

    Product findByName(String name);
}
