package com.example.software.data.service.implementation;

import com.example.software.data.entity.Customer;
import com.example.software.data.entity.Employee;
import com.example.software.data.entity.dto.CustomerDTO;
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
public class CustomerService implements IPerson<CustomerDTO, Long> {

    private final CustomerRepository customerRepository;

    private final CustomerMapper customerMapper;

    @Override
    public CustomerDTO findById(Long id) throws NoSuchElementException {
        return customerRepository
                .findById (id)
                .map (customerMapper::toDto)
                .orElseThrow (NoSuchElementException::new);
    }

    @Override
    public void save(CustomerDTO customerDTO) {

        if (customerDTO == null) {
            log.error ("Customer is null. Are you sure you have connected your form to the application?");
            return;
        }
        customerRepository.save (customerMapper.toCustomer (customerDTO));
    }
    @Override
    public Customer saveCus(Customer customer) {
        customer.getFirstName();
        customer.getLastName();
       return customerRepository.save(customer);
    }

    @Override
    public Employee saveEmplo(Employee employee) {
        return null;
    }

    @Override
    public void delete(CustomerDTO customerDTO) {
        customerRepository.delete (customerMapper.toCustomer (customerDTO));
    }

    @Override
    public int count() {
        return 0;
    }

    @Override
    public List<Employee> find() {
        return null;
    }

    @Override
    public List<CustomerDTO> findAll(String stringFilter) {
        return customerRepository.findAll ()
                                 .stream ()
                                 .map (customerMapper::toDto)
                                 .collect (Collectors.toList ());
    }
}
