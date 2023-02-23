package com.example.software.data.entity.mappers;

import com.example.software.data.entity.Employee;
import com.example.software.data.entity.dto.EmployeeDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper {

    private final ModelMapper modelMapper;

    public EmployeeMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public EmployeeDTO toDto(Employee employee){
        modelMapper.getConfiguration()
                   .setMatchingStrategy(MatchingStrategies.LOOSE);
        return modelMapper.map(employee, EmployeeDTO.class);

    }

    public Employee toEmployee(EmployeeDTO employeeDTO){
        modelMapper.getConfiguration()
                   .setMatchingStrategy(MatchingStrategies.LOOSE);
        return modelMapper.map(employeeDTO, Employee.class);
    }
}
