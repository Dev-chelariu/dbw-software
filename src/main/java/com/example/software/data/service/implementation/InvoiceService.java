package com.example.software.data.service.implementation;

import com.example.software.data.entity.Customer;
import com.example.software.data.entity.Employee;
import com.example.software.data.entity.Invoice;
import com.example.software.data.entity.InvoiceDetails;
import com.example.software.data.repository.EmployeeRepository;
import com.example.software.data.repository.InvoiceDetailRepository;
import com.example.software.data.repository.InvoiceRepository;
import com.example.software.data.service.IInvoice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Slf4j
public class InvoiceService implements IInvoice {

    private final InvoiceRepository invoiceRepository;
    private final EmployeeRepository employeeRepository;
    private final CustomerService customerService;
    private final InvoiceDetailRepository invoiceDetailsRepository;

    public InvoiceService(InvoiceRepository invoiceRepository, EmployeeRepository employeeRepository, CustomerService customerService, InvoiceDetailRepository invoiceDetailsRepository) {
        this.invoiceRepository = invoiceRepository;
        this.employeeRepository = employeeRepository;
        this.customerService = customerService;
        this.invoiceDetailsRepository = invoiceDetailsRepository;
    }

    @Override
    public Invoice getInvoiceById(Long id) {
        return invoiceRepository.findById(id).orElse(null);
    }

    @Override
    public int count() {
       return (int) invoiceRepository.count();
    }

    @Override
    public List<Invoice> findAll() {
        return invoiceRepository.findAll();
    }

    @Transactional @Override
    public Invoice saveInvoice(Invoice invoice, Customer customer) {

        // Get the selected employee from the ComboBox
        Employee selectedEmployee = invoice.getEmployee();

        // Find the employee with the given id
        Optional<Employee> employeeOptional = employeeRepository.findById(selectedEmployee.getId());
        if (employeeOptional.isPresent()) {
            invoice.setEmployee(employeeOptional.get());

        } else {
            throw new NoSuchElementException("Employee with id " + selectedEmployee.getId() + " not found");
        }

         try {
             Customer savedCustomer = customerService.saveNoDtoCustomer(customer);

             invoice.setCustomer(savedCustomer);
             invoice.setName(invoice.getName());
             invoice.setInvoiceDate(invoice.getInvoiceDate());
             invoice.setTotal(invoice.getTotal());
              invoiceRepository.save(invoice);

            } catch (Throwable e) {
                log.error("Invoice or Customer not saved!", e.getMessage());
            }
        return invoice;
    }

    @Transactional @Override
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
}

    @Override
    public BigDecimal getTotalSale() {

        BigDecimal totalSale = invoiceRepository.getTotalSale();
        return totalSale;
    }
}



