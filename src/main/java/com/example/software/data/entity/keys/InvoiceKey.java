package com.example.software.data.entity.keys;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceKey implements Serializable {

    private Long codProduct;

    private Long invoiceId;
}
