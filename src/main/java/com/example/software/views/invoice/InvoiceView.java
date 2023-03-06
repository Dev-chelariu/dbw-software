package com.example.software.views.invoice;

import com.example.software.data.entity.Customer;
import com.example.software.data.entity.Employee;
import com.example.software.data.entity.Product;
import com.example.software.data.service.implementation.EmployeeService;
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
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.StreamResource;

import javax.annotation.security.PermitAll;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@PageTitle("Invoice")
@Route(value = "invoice", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@JsModule("./styles/shared-styles.js")
@PermitAll
public class InvoiceView extends Div {

    private Grid<Product> selectedProductsGrid;

    private final EmployeeService employeeService;

    List<Product> invoiceList = new ArrayList<>();
    Customer customer = new Customer();
    TextField cFirstName, cLastName, cPhone;
    EmailField emailCustomer;
    TextArea textArea;
    PdfUtils pdfUtils = new PdfUtils();
    Span status;
    Binder<Customer> binder = new Binder<>(Customer.class);

    public InvoiceView(EmployeeService employeeService, ProductService productService) {
        this.employeeService = employeeService;
        this.selectedProductsGrid = createSelectedProductsGrid();

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

        Button saveBtn = new Button("Save invoice");
        saveBtn.setThemeName("primary");
        saveBtn.addClickListener(event -> {
            dialog.open();
            status.setVisible(false);
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

            binder.setBean(customer);
            binder.bind(cFirstName, Customer::getFirstName, Customer::setFirstName);
            binder.bind(cLastName, Customer::getLastName, Customer::setLastName);
            binder.bind(emailCustomer, Customer::getEmail, Customer::setEmail);
            binder.bind(cPhone, Customer::getPhone, Customer::setPhone);
            binder.bind(textArea, Customer::getDetails, Customer::setDetails);

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
        TextField invoiceName = new TextField();
        invoiceName.getElement().setAttribute("colspan", "2");
        invoiceName.setLabel("Invoice Name");
        invoiceName.setClassName("large");

        ComboBox<Employee> employeeBring = new ComboBox<>("Employee");
        // Get a list of Employee objects from your service
        List<Employee> employees = employeeService.find();

        // Create a ListDataProvider from the list of Employee objects
        ListDataProvider<Employee> dataProvider = new ListDataProvider<>(employees);

        // Set the dataProvider as the items in the ComboBox
        employeeBring.setDataProvider(dataProvider);

        // Set the label generator for the ComboBox
        employeeBring.setItemLabelGenerator(Employee::getFirstName);

        // Set the placeholder text for the ComboBox
        employeeBring.setPlaceholder("Select an employee");

        // Set the Date for the DatePicker
        DatePicker date = new DatePicker();
        date.setLabel("Date");

        // Inputs
        cFirstName = new TextField();
        cFirstName.getElement().setAttribute("colspan", "2");
        cFirstName.setLabel("First Name");
        cFirstName.setClassName("large");
        // Inputs
        cLastName = new TextField();
        cLastName.getElement().setAttribute("colspan", "2");
        cLastName.setLabel("Last Name");
        cLastName.setClassName("large");

        // Inputs
        emailCustomer = new EmailField();
        emailCustomer.getElement().setAttribute("colspan", "2");
        emailCustomer.setLabel("Email");
        emailCustomer.setClassName("large");

        // Inputs
        cPhone = new TextField();
        cPhone.getElement().setAttribute("colspan", "2");
        cPhone.setPattern(
                "^[+]?[(]?[0-9]{3}[)]?[-s.]?[0-9]{3}[-s.]?[0-9]{4,6}$");
        cPhone.setHelperText("Format: +(123)456-7890");
        cPhone.setLabel("Phone");
        cPhone.setClassName("large");

        //Text Area
        textArea = new TextArea();
        int charLimit = 140;
        textArea.setLabel("Details invoice");
        textArea.setMaxLength(charLimit);
        textArea.setValueChangeMode(ValueChangeMode.EAGER);
        textArea.addValueChangeListener(e -> {
            e.getSource()
                    .setHelperText(e.getValue().length() + "/" + charLimit);
        });
        inputsFormLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 2,
                        FormLayout.ResponsiveStep.LabelsPosition.TOP),
                new FormLayout.ResponsiveStep("20em", 3),
                new FormLayout.ResponsiveStep("30em", 4));
        inputsFormLayout.setId("inputs");

        inputsFormLayout.add(invoiceName, employeeBring, date, cFirstName, cLastName, emailCustomer, cPhone, textArea);
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

        // Grid Pro
        selectedProductsGrid.setItems(invoiceList);
        selectedProductsGrid.setHeight("600px");

        // add the grids to the layout
        add(selectedProductsGrid);

        // create the add transaction button
        addProductToInvoiceBtn.addClickListener(event -> {
            List<Product> allProducts = productService.findAll();

            AddTransactionDialog addTransactionDialog = new AddTransactionDialog(allProducts, selectedProducts -> {
                selectedProductsGrid.setItems(selectedProducts);
                selectedProductsGrid.getDataProvider().refreshAll();
            });
            addTransactionDialog.open();
        });

        selectedProductsGrid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS,
                GridVariant.LUMO_COMPACT);
        selectedProductsGrid.addComponentColumn(item -> createRemoveButton(selectedProductsGrid, item))
                .setWidth("70px").setFlexGrow(0)
                .setTextAlign(ColumnTextAlign.CENTER);

        selectedProductsGrid.setMultiSort(true);

        selectedProductsGrid.getColumns().forEach(column -> column.setResizable(true));
        selectedProductsGrid.setHeight("500px");

        // Details line
        Div detailsLine = new Div();

        Select<String> totalSelect = new Select<>("USD", "EUR", "GBP");
        totalSelect.setValue("EUR");
        totalSelect.getElement().setAttribute("theme", "custom");
        totalSelect.setClassName("currency-selector");

        Span totalText = new Span();
        totalText.setText("Total in ");

        Span priceText = new Span();
        priceText.setClassName("total");
        priceText.setText("812");

        detailsLine.add(totalText, totalSelect, priceText);
        detailsLine.setClassName("controls-line footer");

        exportPdfButton.addClickListener(eventPdf -> {
            // Generate the PDF and store it in a byte array
            byte[] pdfBytes;
            try {
                pdfBytes = pdfUtils.generatePdf(invoiceName, cFirstName, cLastName, date, cPhone, emailCustomer, textArea);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            // Create a StreamResource containing the PDF data
            StreamResource resource = new StreamResource("invoice.pdf", () -> new ByteArrayInputStream(pdfBytes));

            // Create an Anchor component to trigger the download
            Anchor downloadLink = new Anchor(resource, "Download PDF");

            // Set the download attribute to trigger a download when clicked
            downloadLink.getElement().setAttribute("download", true);
            Notification.show("Check in the footer of the page and press download!"
                    , 3000, Notification.Position.MIDDLE);

            // Add the download link to the view
            add(downloadLink);
        });
            add(controlsLine, board, addsLine,selectedProductsGrid, detailsLine);
        }

    private Grid<Product> createSelectedProductsGrid () {
            Grid<Product> grid = new Grid<>(Product.class);
            grid.setItems();
            grid.setColumns("name", "price", "stockCount");
//            grid.addComponentColumn(invoice -> {
//                NumberField quantityField = new NumberField();
//                quantityField.setValue((double) invoice.);
//                quantityField.addValueChangeListener(event -> {
//                    double newValue = event.getValue() != null ? event.getValue() : 0;
//                    invoice.setQuantity((int) newValue);
//                });
//                return quantityField;
//            }).setHeader("Quantity");

            return grid;
        }

        private Button createRemoveButton (Grid < Product > grid, Product item){
            Button button = new Button(new Icon(VaadinIcon.CLOSE), clickEvent -> {
                ListDataProvider<Product> dataProvider = (ListDataProvider<Product>) grid
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
}