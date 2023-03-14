package com.example.software.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Formula;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="dbw_invoice")
public class Invoice  implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "idgenerator")
    @SequenceGenerator(name = "idgenerator", initialValue = 1000)
    @Column(name="invoice_id")
    private Long invoiceId;

    private String name;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    @NotNull
    @JsonIgnoreProperties({"customers"})
    private Customer customer;

    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.ALL}, mappedBy = "invoice")
    private List<InvoiceDetails> invoiceDetails = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "employee_id")
    @NotNull
    @JsonIgnoreProperties({"employees"})
    private Employee employee;

    @Formula("(select count(c.id) from Customer c where c.invoice_id = id)")
    @Transient
    private int customerCount;

    private int total;

    private LocalDate invoiceDate;
}
