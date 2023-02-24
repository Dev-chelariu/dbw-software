package com.example.software.data.entity.dto;

import com.example.software.data.entity.enums.Availability;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * A DTO for the {@link com.example.software.data.entity.Invoice} entity
 */
@Data
public class InvoiceDto implements Serializable {
    private final Long id;
    private final String description;
    private final List<ProductDTO> products;
    private final int amount;
    private final int total;
    @NotNull
    private final Availability availability;
}