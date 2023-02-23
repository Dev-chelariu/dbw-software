package com.example.software.data.service;

import com.example.software.data.entity.dto.CustomerDTO;
import com.example.software.data.entity.dto.EmployeeDTO;

import java.util.List;

public interface IPerson {

    EmployeeDTO getEmployeeById(Long id);

    void addEmployee(EmployeeDTO employeeDTO);

    void deleteEmployee(EmployeeDTO employeeDTO);

    int count();

    List<EmployeeDTO> findAll(String stringFilter);

    CustomerDTO getCustomerById(Long id);

    void addCustomer(CustomerDTO customerDTO);

    void deleteCustomer(CustomerDTO customerDTO);

    List<CustomerDTO> findAll();
}
