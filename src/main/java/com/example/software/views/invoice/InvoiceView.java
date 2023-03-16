package com.example.software.views.invoice;

import com.example.software.data.entity.*;
import com.example.software.data.service.implementation.EmployeeService;
import com.example.software.data.service.implementation.InvoiceService;
import com.example.software.data.service.implementation.ProductService;
import com.example.software.views.MainLayout;
import com.example.software.views.invoice.ExportUtils.PdfUtils;
import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.StreamResource;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.security.PermitAll;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@PageTitle("Invoice")
@Route(value = "invoice", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@JsModule("./styles/shared-styles.js")
@PermitAll
@Slf4j
public class InvoiceView extends Div {

    private Grid<InvoiceDetails> selectedProductsGrid;
    private final InvoiceService invoiceService;
    private final Invoice currentInvoice = new Invoice();
    List<InvoiceDetails> detailsList = new ArrayList<>();
    TextField name = new TextField();
    TextField firstName = new TextField("First Name");
    TextField lastName = new TextField("Last Name");
    TextField phone = new TextField("Phone");
    EmailField email = new EmailField("Email");
    TextArea details = new TextArea("Details");
    DatePicker invoiceDate = new DatePicker();
    ComboBox<Employee> employee = new ComboBox<>("Employee");
    IntegerField quantity = new IntegerField();

    Customer customer = new Customer();
    PdfUtils pdfUtils = new PdfUtils();
    Span totalText = new Span();
    Span status;
    Button saveBtn = new Button("Save invoice", VaadinIcon.CHECK.create());

    public InvoiceView(EmployeeService employeeService, ProductService productService, InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
        this.selectedProductsGrid = createSelectedProductsGrid();

       ListDataProvider<InvoiceDetails> dataProvider = new ListDataProvider<>(new ArrayList<>());
        selectedProductsGrid.setDataProvider(dataProvider);

        Binder<Invoice> cBinder = new BeanValidationBinder<>(Invoice.class);
        cBinder.bind(name, Invoice::getName, Invoice::setName);
        cBinder.bind(invoiceDate, Invoice::getInvoiceDate, Invoice::setInvoiceDate);

        Binder<Customer> custBinder = new BeanValidationBinder<>(Customer.class);
        custBinder.bind(firstName, Customer::getFirstName, Customer::setFirstName);
        custBinder.bind(lastName, Customer::getLastName, Customer::setLastName);
        custBinder.bind(email, Customer::getEmail, Customer::setEmail);
        custBinder.bind(phone,  Customer::getPhone, Customer::setPhone);
        custBinder.bind(details,  Customer::getDetails, Customer::setDetails);

        dataProvider.getItems().addAll(detailsList);

        setId("container");
        // Controls part
        Div controlsLine = new Div();
        controlsLine.addClassName("controls-line");

        // Content
        HorizontalLayout detailsWrapper = new HorizontalLayout();
        detailsWrapper.getThemeList().add("padding");
        detailsWrapper.getThemeList().remove("spacing");
        detailsWrapper.setClassName("invoice-details");
        Span invoiceNameHeader = new Span("New Invoice #");
        detailsWrapper.add(invoiceNameHeader);

        // Buttons
        HorizontalLayout buttonsWrapper = new HorizontalLayout();
        buttonsWrapper.getThemeList().remove("spacing");
        buttonsWrapper.addClassName("controls-line-buttons");

        ConfirmDialog dialog = new ConfirmDialog();
        //SAVE button
        status = new Span();
        status.setVisible(false);
        dialog.setHeader("Unsaved changes");
        dialog.setText(
                "Do you want to save your changes?");

        dialog.setCancelable(true);
        dialog.addCancelListener(event -> setStatus("Canceled"));

        dialog.setConfirmText("Save");
        dialog.addConfirmListener(event -> setStatus("Saved"));

        saveBtn.setThemeName("primary");
        saveBtn.addClickListener(event -> {

            try {
            dialog.open();
                custBinder.writeBean(customer);
            cBinder.writeBean(currentInvoice);

            Notification.show("Customer saved successfully!");

            cBinder.readBean(new Invoice());

            } catch (ValidationException e) {
                throw new RuntimeException(e);
            }
            validateAndSave();

            // Show a success message and clear the form
            status.setText("Invoice saved successfully!");
            status.setVisible(true);
        });
        status = new Span();
        status.setVisible(false);

        ConfirmDialog dialog1 = new ConfirmDialog();
        dialog1.setHeader("Changes deleted!");
        dialog1.setText(
                "Your changes has been deleted.");

        Button discardBtn = new Button("Discard changes");
        discardBtn.setThemeName("error tertiary");
        discardBtn.addClickListener(event -> {

            dialog1.open();
            status.setVisible(false);

            custBinder.bind(firstName, Customer::getFirstName, Customer::setFirstName);
            custBinder.bind(lastName, Customer::getLastName, Customer::setLastName);
            custBinder.bind(email, Customer::getEmail, Customer::setEmail);
            custBinder.bind(phone, Customer::getPhone, Customer::setPhone);
            custBinder.bind(details, Customer::getDetails, Customer::setDetails);

            customer.setFirstName("");
            customer.setLastName("");
            customer.setEmail("");
            customer.setPhone("");
            customer.setDetails("");
        });

        dialog1.setConfirmText("Ok");
        dialog1.addConfirmListener(event -> setStatus("Empty!"));

        Button exportPdfButton = new Button("Export as PDF");
        exportPdfButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY,
                ButtonVariant.LUMO_SUCCESS);

        buttonsWrapper.add(discardBtn, saveBtn, status);
        controlsLine.add(detailsWrapper, buttonsWrapper);

        // Input parts layout
        Board board = new Board();

        FormLayout inputsFormLayout = new FormLayout();
        Div inputsFormWrapper = new Div();
        inputsFormWrapper.add(inputsFormLayout);

        // Inputs
        name.getElement().setAttribute("colspan", "2");
        name.setLabel("Invoice name");
        name.setClassName("large");

        employee.setItems(employeeService.find());
        employee.setItemLabelGenerator(Employee::getFirstName);
        employee.addValueChangeListener(event -> {
            if (event.getValue() != null) {
                currentInvoice.setEmployee(event.getValue());
            }
        });
        // Set the placeholder text for the ComboBox
        employee.setPlaceholder("Select an employee");

        // Set the Date for the DatePicker
        invoiceDate.setLabel("Date");

        // Inputs
        firstName.getElement().setAttribute("colspan", "2");
        firstName.setClassName("large");
        // Inputs
        lastName.getElement().setAttribute("colspan", "2");
        lastName.setClassName("large");

        // Inputs
        email.getElement().setAttribute("colspan", "2");
        email.setClassName("large");

        // Inputs
        phone.getElement().setAttribute("colspan", "2");
        phone.setPattern(
                "^[+]?[(]?[0-9]{3}[)]?[-s.]?[0-9]{3}[-s.]?[0-9]{4,6}$");
        phone.setHelperText("Format: +(123)456-7890");
        phone.setClassName("large");

        //Text Area
        int charLimit = 140;
        details.setLabel("Details invoice");
        details.setMaxLength(charLimit);
        details.setValueChangeMode(ValueChangeMode.EAGER);
        details.addValueChangeListener(e -> {
            e.getSource()
                    .setHelperText(e.getValue().length() + "/" + charLimit);
        });
        inputsFormLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 2,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("20em", 3),
                new FormLayout.ResponsiveStep("30em", 4));
        inputsFormLayout.setId("inputs");

        inputsFormLayout.add(name, employee, invoiceDate, firstName, lastName, email, phone, details);
        board.addRow(inputsFormWrapper);

        // Adds line
        Div addsLine = new Div();
        addsLine.setClassName("controls-line");

        // Buttons
        Div btnWrapper = new Div();
        btnWrapper.setClassName("flex-1");

        Span cardTransactionText = new Span("Add products to invoice");
        Button addProductToInvoiceBtn = new Button(cardTransactionText);
        addProductToInvoiceBtn.setThemeName("tertiary");
        addProductToInvoiceBtn.setId("add-transaction");
        btnWrapper.add(addProductToInvoiceBtn, exportPdfButton);
        addsLine.add(btnWrapper);

        selectedProductsGrid.setHeight("700px");
        // add the grids to the layout
        add(selectedProductsGrid);

        // create the add transaction button
        addProductToInvoiceBtn.addClickListener(event -> {
            List<Product> allProducts = productService.findAll();
            AddTransactionDialog addTransactionDialog = new AddTransactionDialog(allProducts, this::accept);

            addTransactionDialog.open();
        });

        selectedProductsGrid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS,
                GridVariant.LUMO_COMPACT);
        selectedProductsGrid.addComponentColumn(item -> createRemoveButton(selectedProductsGrid, item))
                .setWidth("70px").setFlexGrow(0)
                .setTextAlign(ColumnTextAlign.CENTER);

        selectedProductsGrid.setMultiSort(true);
        selectedProductsGrid.getColumns().forEach(column -> column.setResizable(true));

        // Details line
        Div detailsLine = new Div();

        updateTotal(selectedProductsGrid, totalText);

        detailsLine.setHeight("40px");

        detailsLine.add(totalText);
        detailsLine.setClassName("controls-line footer");

//        exportPdfButton.addClickListener(eventPdf -> {
//            // Generate the PDF and store it in a byte array
//            byte[] pdfBytes;
//            try {
//                pdfBytes = pdfUtils.createPdf(name, firstName, lastName, invoiceDate, phone, email, details, employee, detailsList);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//            // Create a StreamResource containing the PDF data
//            StreamResource resource = new StreamResource("invoice.pdf", () -> new ByteArrayInputStream(pdfBytes));
//
//            // Create an Anchor component to trigger the download
//            Anchor downloadLink = new Anchor(resource, "Download PDF");
//
//            // Set the download attribute to trigger a download when clicked
//            downloadLink.getElement().setAttribute("download", true);
//            Notification.show("Check in the footer of the page and press download!"
//                    , 3000, Notification.Position.MIDDLE);
//
//            // Add the download link to the view
//            add(downloadLink);
//        });
            add(controlsLine, board, addsLine,selectedProductsGrid, detailsLine);
        }

        private Button createRemoveButton (Grid <InvoiceDetails> grid, InvoiceDetails item){
            Button button = new Button(new Icon(VaadinIcon.CLOSE), clickEvent -> {
                ListDataProvider<InvoiceDetails> dataProvider = (ListDataProvider<InvoiceDetails>) grid
                        .getDataProvider();
                dataProvider.getItems().remove(item);
                dataProvider.refreshAll();
            });
            button.setClassName("delete-button");
            button.addThemeName("small");
            return button;
        }

        private void setStatus (String value){
            status.setText("Status: " + value);
            status.setVisible(true);
        }
        private Grid<InvoiceDetails> createSelectedProductsGrid () {
        Grid<InvoiceDetails> grid = new Grid<>(InvoiceDetails.class);
        grid.setItems(detailsList);

            grid.addColumn(details -> details.getProduct().getName())
                    .setHeader("Name");
            grid.addColumn(details -> details.getProduct().getPrice())
                   .setHeader("Price");
            grid.addColumn(details -> details.getProduct().getStockCount())
                    .setHeader("Stock Count");

            // Remove the columns that you don't want to display
            grid.removeColumnByKey("codProduct");
            grid.removeColumnByKey("invoiceId");
            grid.removeColumnByKey("product");
            grid.removeColumnByKey("quantity");
            grid.removeColumnByKey("total");
            grid.removeColumnByKey("invoice");

        grid.addComponentColumn(productInvoice -> {
            quantity = new IntegerField();
            quantity.setValue(productInvoice.getQuantity());
            quantity.addValueChangeListener(event -> {
                int newValue = event.getValue() != null ? event.getValue() : 0;
                productInvoice.setQuantity(newValue);
                productInvoice.setTotal(productInvoice.getProduct().getPrice() * newValue);
                grid.getDataProvider().refreshItem(productInvoice);
                updateTotal(grid, totalText);
            });
            return quantity;
        }).setHeader("Quantity");

        grid.addColumn(details -> details.getTotal())
                    .setHeader("Total");

        return grid;
    }
    private void addSelectedProductsToInvoice(List<Product> selectedProducts) {

        for (Product product : selectedProducts) {
            InvoiceDetails details = new InvoiceDetails();
            details.setCodProduct(product.getCodProduct());
            details.setInvoiceId(currentInvoice.getInvoiceId());
            details.setProduct(product);
            detailsList.add(details);
        }
        selectedProductsGrid.setItems(detailsList);
        selectedProductsGrid.getDataProvider().refreshAll();
    }

    private void accept(List<Product> selectedProducts) {
        addSelectedProductsToInvoice(selectedProducts);
       currentInvoice.getInvoiceDetails().addAll(selectedProductsGrid.getSelectedItems());
    }
    private void validateAndSave() {
          invoiceService.saveInvoice(currentInvoice, customer);
          invoiceService.saveInvoiceDetails(currentInvoice, detailsList);
    }

    private void updateTotal(Grid<InvoiceDetails> grid, Span totalText) {

        // Calculate and set the initial total
        int newTotal = detailsList.stream().mapToInt(InvoiceDetails::getTotal).sum();
        totalText.setText(" Total: " + newTotal + " lei");

        // Update the total whenever the quantity of a product changes
        grid.getDataProvider().addDataProviderListener(event -> {
            int updatedTotal = detailsList.stream().mapToInt(InvoiceDetails::getTotal).sum();
            totalText.setText(" Total: " + updatedTotal + " lei");
        });
    }
}