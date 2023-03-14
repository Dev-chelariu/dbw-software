package com.example.software.data.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="dbw_employee")
public class Employee extends Person{

    private LocalDate dateOfBirth;

    @NotEmpty
    private String occupation;

    @OneToMany(fetch = FetchType.EAGER,mappedBy = "employee")
    private List<Invoice> invoices = new LinkedList<>();
    public Employee(Long id) {
        super(id);
    }
}
