package com.example.software.data.entity.mappers;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {

    private final ModelMapper modelMapper;

    public ProductMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

//    public ProductDTO toDto(Product product){
//        modelMapper.getConfiguration()
//                   .setMatchingStrategy(MatchingStrategies.LOOSE);
//        return modelMapper.map(product, ProductDTO.class);
//
//    }
//
//    public Product toProduct(ProductDTO productDTO){
//        modelMapper.getConfiguration()
//                   .setMatchingStrategy(MatchingStrategies.LOOSE);
//        return modelMapper.map(productDTO, Product.class);
//    }
}
