package com.example.software.data.service.implementation;

import com.example.software.data.repository.InvoiceRepository;
import com.example.software.data.service.IInvoice;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InvoiceService implements IInvoice {

    private final InvoiceRepository invoiceRepository;

    @Override
    public int count() {
       return (int) invoiceRepository.count();
    }
}
