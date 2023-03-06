package com.example.software.data.service.implementation;

import com.example.software.data.entity.Invoice;
import com.example.software.data.repository.InvoiceRepository;
import com.example.software.data.service.IInvoice;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InvoiceService implements IInvoice {

    private final InvoiceRepository invoiceRepository;

    @Override
    public int count() {
       return (int) invoiceRepository.count();
    }

    @Override
    public List<Invoice> findAll() {
        return invoiceRepository.findAll();
    }
}
