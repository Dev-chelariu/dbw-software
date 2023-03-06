package com.example.software.data.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.annotation.Nullable;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.validation.constraints.Size;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Customer extends Person {

    @Size(max = 255)
    private String details;

    @OneToMany(mappedBy = "customer")
    @Nullable
    private List<Invoice> employees = new LinkedList<>();
}
