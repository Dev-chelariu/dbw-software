package com.example.software.data.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import java.time.LocalDate;

@Data
public class EmployeeDTO {

    @JsonProperty(value = "id")
    private Long id;

    @JsonProperty(value = "firstname")
    private String firstName;

    @JsonProperty(value = "lastName")
    private String lastName;

    @JsonProperty(value = "email")
    @Email
    private String email;

    @JsonProperty(value = "phone")
    private String phone;

    @JsonProperty(value = "dateOfBirth")
    private LocalDate dateOfBirth;

    @JsonProperty(value = "occupation")
    private String occupation;
}
