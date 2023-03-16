package com.example.software.data.entity;

import com.example.software.data.entity.enums.Availability;
import com.example.software.data.entity.enums.UM;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Set;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="dbw_product")
public class Product implements Serializable {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "cod_product")
    private Long codProduct =-1L;

    @Size(min = 2, message = "Product name must have at least two characters")
    @NotNull
    private String name = "";

    @NotNull
    @Min(0)
    private Integer price = 0;

    @NotNull
    private String category = "";

    private String description;

    @Enumerated(EnumType.STRING)
    private UM unitMeasure;

    @Min(value = 0, message = "Can't have negative amount in stock")
    private int stockCount = 0;

    @NotNull
    private Availability availability = Availability.COMING;

    @OneToMany(fetch = FetchType.EAGER, cascade = {CascadeType.ALL}, mappedBy = "product")
    @JsonIgnore
    private Set<InvoiceDetails> invoiceDetails;

}
