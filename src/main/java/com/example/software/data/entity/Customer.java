package com.example.software.data.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import java.util.LinkedList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="dbw_customer")
public class Customer extends Person {

    @Size(max = 255)
    private String details;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "customer")
    private List<Invoice> invoices = new LinkedList<>();

    public Customer(Long id) {
        super(id);
    }
}
