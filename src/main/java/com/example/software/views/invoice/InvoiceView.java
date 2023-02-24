package com.example.software.views.invoice;

import com.example.software.data.entity.Invoice;
import com.example.software.data.entity.enums.Availability;
import com.example.software.data.entity.enums.Currency;
import com.example.software.views.MainLayout;
import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.gridpro.GridPro;
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
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.TemplateRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.StreamResource;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import javax.annotation.security.PermitAll;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.example.software.data.entity.enums.Currency.getRandomCurrency;

@PageTitle("Invoice")
@Route(value = "invoice", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@JsModule("./styles/shared-styles.js")
@PermitAll
public class InvoiceView extends Div {
    private Anchor anchor;

    public InvoiceView() {
        setId("container");

        // Controls part
        Div controlsLine = new Div();
        controlsLine.addClassName("controls-line");


        // Content
        HorizontalLayout detailsWrapper = new HorizontalLayout();
        detailsWrapper.getThemeList().add("padding");
        detailsWrapper.getThemeList().remove("spacing");
        detailsWrapper.setClassName("invoice-details");

        Span invoiceNameHeader = new Span("Invoice #3225");

        detailsWrapper.add(invoiceNameHeader);

        // Buttons
        HorizontalLayout buttonsWrapper = new HorizontalLayout();
        buttonsWrapper.getThemeList().remove("spacing");
        buttonsWrapper.addClassName("controls-line-buttons");

        Button discardBtn = new Button("Discard changes",
                e -> Notification.show("Changes were discarded!"));
        discardBtn.setThemeName("error tertiary");

        Button saveDraftBtn = new Button("Save draft",
                e -> Notification.show("Changes were saved!"));
        saveDraftBtn.setThemeName("tertiary");

        Button sendBtn = new Button("Send",
                e -> Notification.show("Invoice was sent!"));
        sendBtn.setThemeName("primary");

        Button exportPdfButton = new Button("Export as PDF");

        exportPdfButton.addClickListener(eventPdf -> {
            try {
                // Generate the invoice PDF using PDFBox
                PDDocument document = new PDDocument();
                PDPage page = new PDPage();
                document.addPage(page);
                PDPageContentStream contentStream = new PDPageContentStream(document, page);

                // Add content to the PDF document
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.newLineAtOffset(100, 700);
                contentStream.showText("Invoice");

                contentStream.endText();

                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.newLineAtOffset(100, 650);
                //   contentStream.showText("Customer Name: " + customerNameField.getValue());
                contentStream.endText();

                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.newLineAtOffset(100, 600);
                //  contentStream.showText("Amount: " + totalAmountField.getValue());
                contentStream.endText();

                contentStream.close();
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                document.save(out);
                document.close();
                InputStream in = new ByteArrayInputStream(out.toByteArray());

                // Download the generated PDF file
                StreamResource resource = new StreamResource("invoice.pdf", () -> in);
                anchor = new Anchor(resource, "Download");
                anchor.getElement().setAttribute("download", true);
                Notification.show("Check in the footer of the page and press download!"
                        , 3000, Notification.Position.MIDDLE);
                add(anchor);

            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        buttonsWrapper.add(discardBtn, saveDraftBtn, sendBtn, exportPdfButton);

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
        invoiceName.setValue("Trip to Italy");

        Select<String> employee = new Select<>("Manolo", "Joonas", "Matti");
        employee.setValue("Manolo");
        employee.setLabel("Employee");

        DatePicker date = new DatePicker();
        date.setValue(LocalDate.of(2018, 12, 12));
        date.setLabel("Date");

        // Inputs
        TextField cFirstName = new TextField();
        cFirstName.getElement().setAttribute("colspan", "2");
        cFirstName.setLabel("Customer: First Name");
        cFirstName.setClassName("large");
        cFirstName.setValue(" Italy");
        // Inputs
        TextField cLastName = new TextField();
        cLastName.getElement().setAttribute("colspan", "2");
        cLastName.setLabel("Customer: Last Name");
        cLastName.setClassName("large");
        cLastName.setValue(" Italy");

        // Inputs
        EmailField emailCustomer = new EmailField();
        emailCustomer.getElement().setAttribute("colspan", "2");
        emailCustomer.setLabel("Customer email");
        emailCustomer.setClassName("large");

        // Inputs
        TextField cPhone = new TextField();
        cPhone.getElement().setAttribute("colspan", "2");
        cPhone.setPattern(
                "^[+]?[(]?[0-9]{3}[)]?[-s.]?[0-9]{3}[-s.]?[0-9]{4,6}$");
        cPhone.setHelperText("Format: +(123)456-7890");
        cPhone.setLabel("Customer: Phone");
        cPhone.setClassName("large");

        //Text Area
        TextArea textArea = new TextArea();
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

        inputsFormLayout.add(invoiceName, employee, date, cFirstName, cLastName, emailCustomer, cPhone, textArea);

        board.addRow(inputsFormWrapper);

        // Adds line
        Div addsLine = new Div();
        addsLine.setClassName("controls-line");

        // Buttons
        Div btnWrapper = new Div();
        btnWrapper.setClassName("flex-1");

        Span cardTransactionText = new Span("Add products to transaction");
        Button addCardTransactionBtn = new Button(cardTransactionText);
        addCardTransactionBtn.setThemeName("tertiary");
        addCardTransactionBtn.setId("add-transaction");
        btnWrapper.add(addCardTransactionBtn);

        addsLine.add(btnWrapper);

//        // Grid Pro
        GridPro<Invoice> grid = new GridPro<>();
        List<Invoice> invoiceList = createItems();
        grid.setItems(invoiceList);

        addCardTransactionBtn.addClickListener(e -> {
            invoiceList.add(0, new Invoice("","", 0, 0, Currency.EUR,false,0, Availability.COMING));
            grid.getDataProvider().refreshAll();
        });

        grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS,
                GridVariant.LUMO_COMPACT);

        grid.addEditColumn(Invoice::getProduct, "product")
                .text((item, newValue) -> {
                    item.setProduct(newValue);
                    displayNotification("Product", item, newValue);
                }).setHeader("Product");

        grid.addEditColumn(Invoice::getDescription, "description")
                .text((item, newValue) -> {
                    item.setDescription(newValue);
                    displayNotification("Description", item, newValue);
                }).setHeader("Description").setWidth("250px");

        grid.addEditColumn(Invoice::getPrice, "price")
                .text((item, newValue) -> {
                    try {
                        item.setPrice(Float.valueOf(newValue));
                        displayNotification("Price", item, newValue);
                    } catch (Exception e) {
                        displayNotification("Price", item);
                    }
                }).setHeader("Price").setTextAlign(ColumnTextAlign.END);

        ComponentRenderer<Div, Invoice> currencyRenderer = new ComponentRenderer<>(
                invoice -> {
                    Div icon = new Div();
                    icon.setText(invoice.getCurrency().name());
                    icon.setClassName("icon-"
                            + invoice.getCurrency().name().toLowerCase());
                    return icon;
                });

        grid.addEditColumn(Invoice::getCurrency, currencyRenderer)
                .select((item, newValue) -> {
                    item.setCurrency(newValue);
                    displayNotification("Currency", item,
                            newValue.getStringRepresentation());
                }, Currency.class)
                .setComparator(Comparator.comparing(
                        inv -> inv.getCurrency().getStringRepresentation()))
                .setHeader("Currency").setWidth("150px");

        grid.addEditColumn(Invoice::getAmount, "amount")
                .text((item, newValue) -> {
                    try {
                        item.setAmount(Integer.valueOf(newValue));
                        displayNotification("Amount", item, newValue);
                    } catch (Exception e) {
                        displayNotification("Amount", item);
                    }
                }).setHeader("Amount").setTextAlign(ColumnTextAlign.END);

        ComponentRenderer<Span, Invoice> statusRenderer = new ComponentRenderer<>(
                invoice -> {
                    Span badge = new Span();
                    badge.setText(
                            invoice.getOrderCompleted() ? "Completed" : "Open");
                    badge.getElement().setAttribute("theme",
                            invoice.getOrderCompleted() ? "badge success"
                                    : "badge");
                    return badge;
                });
        grid.addEditColumn(Invoice::getOrderCompleted, statusRenderer)
                .checkbox((item, newValue) -> {
                    item.setOrderCompleted(newValue);
                    displayNotification("Order completed ", item,
                            newValue.toString());
                })
                .setComparator(
                        Comparator.comparing(inv -> inv.getOrderCompleted()))
                .setHeader("Status");
        grid.addEditColumn(Invoice::getTotal,
                        TemplateRenderer.<Invoice> of("[[item.symbol]][[item.total]]")
                                .withProperty("symbol",
                                        invoice -> invoice.getCurrency().getSymbol())
                                .withProperty("total", Invoice::getTotal))
                .text((item, newValue) -> {
                    try {
                        item.setTotal(Integer.parseInt(newValue));
                        displayNotification("Total", item, newValue);
                    } catch (Exception e) {
                        displayNotification("Total", item);
                    }
                }).setComparator(Comparator.comparing(inv -> inv.getTotal()))
                .setHeader("Total").setTextAlign(ColumnTextAlign.END);
        grid.addComponentColumn(item -> createRemoveButton(grid, item))
                .setWidth("70px").setFlexGrow(0)
                .setTextAlign(ColumnTextAlign.CENTER);

        grid.setMultiSort(true);

        grid.getColumns().forEach(column -> column.setResizable(true));

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

        add(controlsLine, board, addsLine,grid, detailsLine);
    }

    private Button createRemoveButton(GridPro<Invoice> grid, Invoice item) {
        Button button = new Button(new Icon(VaadinIcon.CLOSE), clickEvent -> {
            ListDataProvider<Invoice> dataProvider = (ListDataProvider<Invoice>) grid
                    .getDataProvider();
            dataProvider.getItems().remove(item);
            dataProvider.refreshAll();
        });
        button.setClassName("delete-button");
        button.addThemeName("small");
        return button;
    }

    private static void displayNotification(String propertyName, Invoice item,
                                            String newValue) {
        Notification.show(propertyName + " was updated to be: " + newValue
                + " for product " + item.toString());
    }

    private static void displayNotification(String propertyName, Invoice item) {
        Notification.show(
                propertyName + " cannot be set for product " + item.toString());
    }

    private static List<Invoice> createItems() {
        Random random = new Random(0);
        return IntStream.range(1, 14)
                .mapToObj(index -> createInvoice(index, random))
                .collect(Collectors.toList());
    }

    private static Invoice createInvoice(int index, Random random) {
        Invoice invoice = new Invoice();
        invoice.setProduct("PVR2019");
        invoice.setDescription("Say Dock");
        invoice.setPrice(random.nextInt(100000) / 100f);
        invoice.setCurrency(getRandomCurrency());
        invoice.setAmount(1 + random.nextInt((10 - 1) + 1));
        invoice.setOrderCompleted(random.nextBoolean());
        invoice.setAvailability(Availability.COMING);
        invoice.setTotal(1 + random.nextInt((1000 - 1) + 1));

        return invoice;
    }
}
