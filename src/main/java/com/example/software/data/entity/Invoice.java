package com.example.software.data.entity;

import com.example.software.data.entity.enums.Availability;
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

    @ManyToMany(fetch=FetchType.EAGER)
    @JoinTable(name="invoice_details",
            joinColumns=
            @JoinColumn(name="invoice_id", referencedColumnName="invoice_id"),
            inverseJoinColumns=
            @JoinColumn(name="cod_product", referencedColumnName="cod_product"))
    private List<Product> lstProduct = new ArrayList<>();

    @Formula("(select count(c.id) from Customer c where c.invoice_id = id)")
    private int customerCount;

    private int quantity;

    private Boolean isOrderCompleted;

    private int total;

    private LocalDate invoiceDate;

    @NotNull
    private Availability availability = Availability.COMING;


//    public List<Product> getProducts() {
//        return products;
//    }

    @Override
    public Invoice clone() {
        try {
            return (Invoice) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(
                    "The Invoices object could not be cloned.", e);
        }
    }
}
