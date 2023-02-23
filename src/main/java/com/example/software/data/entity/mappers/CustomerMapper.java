package com.example.software.data.entity.mappers;

import com.example.software.data.entity.Customer;
import com.example.software.data.entity.dto.CustomerDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper {

    private final ModelMapper modelMapper;

    public CustomerMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public CustomerDTO toDto(Customer customer){
        modelMapper.getConfiguration()
                   .setMatchingStrategy(MatchingStrategies.LOOSE);
        return modelMapper.map(customer, CustomerDTO.class);

    }

    public Customer toCustomer(CustomerDTO customerDTO){
        modelMapper.getConfiguration()
                   .setMatchingStrategy(MatchingStrategies.LOOSE);
        return modelMapper.map(customerDTO, Customer.class);
    }
}
