package com.example.software.data.service;

import com.example.software.data.entity.Customer;
import com.example.software.data.entity.Invoice;
import com.example.software.data.entity.InvoiceDetails;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

public interface IInvoice {

    Invoice getInvoiceById(Long id);

    int count();
    List<Invoice> findAll();

    @Transactional
    Invoice saveInvoice(Invoice invoice, Customer customer);

    @Transactional
    void saveInvoiceDetails(Invoice invoice, List<InvoiceDetails> detailsList);

    BigDecimal getTotalSale();
}
