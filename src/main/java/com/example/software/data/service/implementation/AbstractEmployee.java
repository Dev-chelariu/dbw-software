package com.example.software.data.service.implementation;

import com.example.software.data.entity.Employee;
import com.example.software.data.entity.dto.EmployeeDTO;
import com.example.software.data.entity.mappers.EmployeeMapper;
import com.example.software.data.repository.EmployeeRepository;
import com.example.software.data.service.IPerson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Slf4j
public abstract class AbstractEmployee implements IPerson {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    protected AbstractEmployee(EmployeeRepository employeeRepository, EmployeeMapper employeeMapper) {
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
    }

    @Override
    public EmployeeDTO getEmployeeById(Long id) throws NoSuchElementException {
        return employeeRepository
                .findById(id)
                .map (employeeMapper::toDto)
                .orElseThrow (NoSuchElementException::new);
    }

    @Override
    public void addEmployee(EmployeeDTO employeeDTO) {

        if (employeeDTO == null) {
           log.error ("Employee is null. Are you sure you have connected your form to the application?");
            return;
        }
         employeeRepository.save(employeeMapper.toEmployee (employeeDTO));
    }

    @Override
    public void deleteEmployee(EmployeeDTO employeeDTO) {
        employeeRepository.delete(employeeMapper.toEmployee (employeeDTO));

    }

    public Page<Employee> list(Pageable pageable) {
        return employeeRepository.findAll(pageable);
    }

    public Page<Employee> list(Pageable pageable, Specification<Employee> filter) {
        return employeeRepository.findAll(filter, pageable);
    }

    @Override
    public int count() {
        return (int) employeeRepository.count();
    }

    @Override
    public List<EmployeeDTO> findAll(String stringFilter) {
        if (stringFilter == null || stringFilter.isEmpty()) {
            return employeeRepository.findAll().stream ().map (employeeMapper::toDto)
                    .collect(Collectors.toList());
        } else {
            return employeeRepository.search(stringFilter).stream ().map (employeeMapper::toDto)
                    .collect(Collectors.toList());
        }
    }
}
