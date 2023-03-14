package com.example.software.data.entity;

import com.example.software.data.entity.keys.InvoiceKey;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter @Setter
@Entity
@IdClass(InvoiceKey.class)
@Table(name = "dbw_invoice_product")
public class InvoiceDetails {

    @Id
    @Column(name = "cod_product")
    private Long codProduct;

    @Id
    @Column(name = "invoice_id")
    private Long invoiceId;

    private int quantity;

    private int total;

    @ManyToOne
    @JoinColumn(name = "cod_product", referencedColumnName = "cod_product",
            insertable = false, updatable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "invoice_id", referencedColumnName = "invoice_id",
            insertable = false, updatable = false)
    private Invoice invoice;
}
