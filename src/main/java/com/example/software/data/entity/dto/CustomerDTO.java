package com.example.software.data.entity.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

@Data
public class CustomerDTO {

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

    @JsonProperty(value = "details")
    @Size(max = 255)
    private String details;
}
