package com.example.software.data.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Employee extends Person{

    private LocalDate dateOfBirth;

    @NotEmpty
    private String occupation;
}
