package com.example.software.data.service.implementation;

import com.example.software.data.entity.dto.CustomerDTO;
import com.example.software.data.entity.mappers.EmployeeMapper;
import com.example.software.data.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EmployeeService extends AbstractEmployee{

    @Autowired
    public EmployeeService(EmployeeRepository employeeRepository, EmployeeMapper employeeMapper) {
        super(employeeRepository, employeeMapper);
    }


    @Override
    public CustomerDTO getCustomerById(Long id) {
        return null;
    }

    @Override
    public void addCustomer(CustomerDTO customerDTO) {

    }

    @Override
    public void deleteCustomer(CustomerDTO customerDTO) {

    }

    @Override
    public List<CustomerDTO> findAll() {
        return null;
    }
}
