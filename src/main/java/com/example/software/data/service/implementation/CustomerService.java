package com.example.software.data.service.implementation;

import com.example.software.data.entity.dto.CustomerDTO;
import com.example.software.data.entity.dto.EmployeeDTO;
import com.example.software.data.entity.mappers.CustomerMapper;
import com.example.software.data.repository.CustomerRepository;
import com.example.software.data.service.IPerson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomerService implements IPerson {

    private final CustomerRepository customerRepository;

    private final CustomerMapper customerMapper;

    @Override
    public CustomerDTO getCustomerById(Long id) throws NoSuchElementException {
        return customerRepository
                .findById (id)
                .map (customerMapper::toDto)
                .orElseThrow (NoSuchElementException::new);
    }

    @Override
    public void addCustomer(CustomerDTO customerDTO) {

        if (customerDTO == null) {
            log.error ("Customer is null. Are you sure you have connected your form to the application?");
            return;
        }
        customerRepository.save (customerMapper.toCustomer (customerDTO));
    }

    @Override
    public void deleteCustomer(CustomerDTO customerDTO) {
        customerRepository.delete (customerMapper.toCustomer (customerDTO));
    }

    @Override
    public EmployeeDTO getEmployeeById(Long id) {
        return null;
    }

    @Override
    public void addEmployee(EmployeeDTO employeeDTO) {

    }

    @Override
    public void deleteEmployee(EmployeeDTO employeeDTO) {

    }

    @Override
    public int count() {
        return 0;
    }

    @Override
    public List<EmployeeDTO> findAll(String stringFilter) {
        return null;
    }

    @Override
    public List<CustomerDTO> findAll() {
        return customerRepository.findAll ()
                                 .stream ()
                                 .map (customerMapper::toDto)
                                 .collect (Collectors.toList ());
    }
}
