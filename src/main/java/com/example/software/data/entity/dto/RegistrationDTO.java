package com.example.software.data.entity.dto;

import com.example.software.data.entity.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.ElementCollection;
import javax.persistence.FetchType;
import java.util.Set;

@Data
public class RegistrationDTO {

    @JsonProperty(value = "username")
    private String username;

    @JsonProperty(value = "name")
    private String name;

    @JsonProperty(value = "hashedPassword")
    @JsonIgnore
    private String hashedPassword;

    @JsonProperty(value = "roles")
    @ElementCollection(fetch = FetchType.EAGER)
    private Set<Role> roles;
}
