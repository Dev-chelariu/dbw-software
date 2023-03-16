package com.example.software.views.invoice;

import com.example.software.data.entity.Invoice;
import com.example.software.data.entity.InvoiceDetails;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;

public class InvoiceDetailsForm  extends FormLayout{

    private final Grid<InvoiceDetails> productInvoiceGrid;

    TextField name = new TextField();
    TextField firstName = new TextField();
    TextField lastName = new TextField();
    TextField phone = new TextField();
    EmailField email = new EmailField();
    TextArea details = new TextArea();
    IntegerField total = new IntegerField();
    DatePicker invoiceDate = new DatePicker();

    TextField employee = new TextField();

    public InvoiceDetailsForm() {

        // Create a FlexLayout to hold the grid and other fields
        FlexLayout layout = new FlexLayout();
        layout.setWidthFull();
        layout.setAlignItems(FlexComponent.Alignment.START);
        add(layout);

        productInvoiceGrid = new Grid<>(InvoiceDetails.class, false);
        productInvoiceGrid.addColumn(productInvoice -> productInvoice.getProduct().getName()).setHeader("Product");
        productInvoiceGrid.addColumn(InvoiceDetails::getQuantity).setHeader("Quantity");
        productInvoiceGrid.addColumn(InvoiceDetails::getTotal).setHeader("Total");

        productInvoiceGrid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);
        productInvoiceGrid.setHeight("260px");

        add(productInvoiceGrid);

        // Add the other fields to the FlexLayout
        FormLayout formLayout = new FormLayout();
        formLayout.setWidthFull();
        layout.add(formLayout);

        formLayout.addFormItem(firstName, "First Name");
        formLayout.addFormItem(lastName, "Last Name");
        formLayout.addFormItem(phone, "Phone");
        formLayout.addFormItem(email, "Email");
        formLayout.addFormItem(details, "Details customer");
        formLayout.addFormItem(employee, "Employee");

                formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("400px", 2, FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("800px", 3, FormLayout.ResponsiveStep.LabelsPosition.ASIDE)
        );
    }

    public void setInvoice(Invoice invoice) {
        name.setValue(invoice.getName());
        invoiceDate.setValue(invoice.getInvoiceDate());
        total.setValue(invoice.getTotal());
        firstName.setValue(invoice.getCustomer().getFirstName());
        lastName.setValue(invoice.getCustomer().getLastName());
        phone.setValue(invoice.getCustomer().getPhone());
        email.setValue(invoice.getCustomer().getEmail());
        details.setValue(invoice.getCustomer().getDetails());
        productInvoiceGrid.setItems(invoice.getInvoiceDetails());
        employee.setValue(invoice.getEmployee().getFirstName());
    }



}

