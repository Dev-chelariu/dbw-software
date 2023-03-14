package com.example.software.data.repository;

import com.example.software.data.entity.InvoiceDetails;
import com.example.software.data.entity.keys.InvoiceKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvoiceDetailRepository extends JpaRepository<InvoiceDetails, InvoiceKey> {

}
