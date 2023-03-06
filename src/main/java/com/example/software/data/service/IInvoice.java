package com.example.software.data.service;

import com.example.software.data.entity.Invoice;

import java.util.List;

public interface IInvoice {

    int count();
    List<Invoice> findAll();
}
