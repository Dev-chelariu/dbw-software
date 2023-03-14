package com.example.software.data.service.implementation;

import com.example.software.data.entity.Customer;
import com.example.software.data.entity.Employee;
import com.example.software.data.entity.Invoice;
import com.example.software.data.entity.InvoiceDetails;
import com.example.software.data.repository.CustomerRepository;
import com.example.software.data.repository.EmployeeRepository;
import com.example.software.data.repository.InvoiceDetailRepository;
import com.example.software.data.repository.InvoiceRepository;
import com.example.software.data.service.IInvoice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceService implements IInvoice {

    private final InvoiceRepository invoiceRepository;
    private final EmployeeRepository employeeRepository;
    private final CustomerRepository customerRepository;
    private final InvoiceDetailRepository invoiceDetailsRepository;

    @Override
    public int count() {
       return (int) invoiceRepository.count();
    }

    @Override
    public List<Invoice> findAll() {
        return invoiceRepository.findAll();
    }

     @Transactional
    public Invoice saveInvoice(Invoice invoice, Customer customer) {

        invoice.setCustomer(customer);
        if (customer.getId() == null) {
            try {
                customer.setFirstName(invoice.getCustomer().getFirstName());
                 customer.setLastName(invoice.getCustomer().getLastName());
                 customer.setPhone(invoice.getCustomer().getPhone());
                 customer.setEmail(invoice.getCustomer().getEmail());
                customer.setDetails(invoice.getCustomer().getDetails());
                 customerRepository.save(customer);
            } catch (Throwable e) {
                log.error("Customer not saved!", e.getMessage());
            }
        }
        // Get the selected employee from the ComboBox
        Employee selectedEmployee = invoice.getEmployee();

        // Find the employee with the given id
        Optional<Employee> employeeOptional = employeeRepository.findById(selectedEmployee.getId());
        if (employeeOptional.isPresent()) {
            invoice.setEmployee(employeeOptional.get());

        } else {
            throw new NoSuchElementException("Employee with id " + selectedEmployee.getId() + " not found");
        }
        //save aditional fields from invoice
        invoice.setName(invoice.getName());
        invoice.setInvoiceDate(invoice.getInvoiceDate());

        try {
            invoice = invoiceRepository.save(invoice);

        } catch (Throwable e) {
            log.error("Invoice or invoice details not saved!", e.getMessage());
        }
        return invoice;
    }

    @Transactional
    public void saveInvoiceDetails(Invoice invoice, List<InvoiceDetails> detailsList) {

        // Set the invoice ID for each detail object
        for (InvoiceDetails details : detailsList) {
            details.setInvoiceId(invoice.getInvoiceId());
        }
        try {
            invoiceDetailsRepository.saveAll(detailsList);
        } catch (Exception e) {
            log.error("Error saving invoice details: ", e);
            throw new RuntimeException("Failed to save invoice details.", e);
        }
}   }



