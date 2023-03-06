package com.example.software.data.service.implementation;

import com.example.software.data.entity.Product;
import com.example.software.data.entity.dto.ProductDTO;
import com.example.software.data.entity.mappers.ProductMapper;
import com.example.software.data.repository.ProductRepository;
import com.example.software.data.service.IProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService implements IProduct {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public List<ProductDTO> findAllProducts() {
        return productRepository.findAll().stream ().map (productMapper::toDto)
                                .collect(Collectors.toList());
    }
    @Override
    public List<Product> findAll(){
        return productRepository.findAll();
    }

    @Override
    public ProductDTO addProduct(ProductDTO productDto) {

         productRepository.save (productMapper.toProduct(productDto));

        return null;
    }

    @Override
    public ProductDTO getProductById(Long id) throws NoSuchElementException {
       return productRepository
                .findById (id)
                .map (productMapper::toDto)
                .orElseThrow (NoSuchElementException::new);
    }


    @Override
    public ProductDTO update(ProductDTO productDto) {
        Optional<Product> oProduct = productRepository.findById(productDto.getCodProduct ());

        if (oProduct.isEmpty())
            throw new NoSuchElementException(" Product with id " + productDto.getCodProduct () + " does not exist");

         productRepository.save (productMapper.toProduct(productDto));

         return null;
    }

    @Override
    public ProductDTO delete(ProductDTO productDTO) {

        productRepository.delete(productMapper.toProduct (productDTO));

        return null;
    }

    @Override
    public Page<Product> list(Pageable pageable) {
        return productRepository.findAll(pageable);
    }


    public int count() {
        return (int) productRepository.count();
    }

    @Override
    public List<ProductDTO> findByNameContainingIgnoreCase(String name) throws NoSuchElementException {
        return productRepository
                .findByNameContainingIgnoreCase(name)
                .stream().map (productMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Product findByName(String name) {
        return productRepository.findByName(name);
    }
}
