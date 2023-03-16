package com.example.software.data.repository;

import com.example.software.data.entity.Invoice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    @EntityGraph(attributePaths = {"customer", "employee", "invoiceDetails", "invoiceDetails.product"})
    List<Invoice> findAll();
}
