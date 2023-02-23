package com.example.software.data.service.implementation;

import com.example.software.data.entity.dto.EmployeeDTO;
import com.example.software.data.entity.mappers.EmployeeMapper;
import com.example.software.data.repository.EmployeeRepository;
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
public class EmployeeService implements IPerson<EmployeeDTO, Long> {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    public EmployeeDTO findById(Long id) throws NoSuchElementException {
        return employeeRepository
                .findById(id)
                .map (employeeMapper::toDto)
                .orElseThrow (NoSuchElementException::new);
    }

    public void save(EmployeeDTO employeeDTO) {

        if (employeeDTO == null) {
           log.error ("Employee is null. Are you sure you have connected your form to the application?");
            return;
        }
         employeeRepository.save(employeeMapper.toEmployee (employeeDTO));
    }

    public void delete(EmployeeDTO employeeDTO) {
        employeeRepository.delete(employeeMapper.toEmployee (employeeDTO));

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
