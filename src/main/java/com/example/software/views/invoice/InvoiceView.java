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
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import java.io.IOException;
import javax.annotation.security.PermitAll;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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

        Span invoiceNameHeader = new Span("New Invoice #1");
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

        // Grid Pro
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

        exportPdfButton.addClickListener(eventPdf -> {
            try {
                // Generate the invoice PDF using PDFBox
                PDDocument document = new PDDocument();
                PDPage page = new PDPage(PDRectangle.A4);
                document.addPage(page);

                // Set up a content stream for the page
                PDPageContentStream contentStream = new PDPageContentStream(document, page);

                // Set the font
                PDFont font = PDType1Font.HELVETICA;
                contentStream.setFont(font, 12);

                // Add content to the PDF document
                contentStream.beginText();
                contentStream.newLineAtOffset(275, 750);
                contentStream.showText(invoiceName.getValue());
                contentStream.endText();

                // Add content to the PDF document
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 720);
                contentStream.showText("Nr. crt");
                contentStream.endText();

                contentStream.beginText();
                contentStream.newLineAtOffset(50, 700);
                contentStream.showText("Date: " + date.getValue());
                contentStream.endText();

                contentStream.beginText();
                contentStream.newLineAtOffset(50, 675);
                contentStream.showText("First Name: " + cFirstName.getValue());
                contentStream.endText();

                contentStream.beginText();
                contentStream.newLineAtOffset(50, 650);
                contentStream.showText("Last Name: " + cLastName.getValue());
                contentStream.endText();

                // Write the customer email
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 625);
                contentStream.showText("Email: " + emailCustomer.getValue());
                contentStream.endText();

                // Write the customer phone
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 600);
                contentStream.showText("Phone: " + cPhone.getValue());
                contentStream.endText();

                // Write the invoice details
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 575);
                contentStream.showText("Details: " + textArea.getValue());
                contentStream.endText();

                // Write the employee responsible
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 550);
                contentStream.showText("Employee: " + employee.getValue());
                contentStream.endText();

                // set table parameters  //table
                float margin = 65;
                float yStartNewPage = page.getMediaBox().getHeight() - (5 * margin);
                float tableWidth = page.getMediaBox().getWidth() - (1 * margin);
                float rowHeight = 20f;
                float tableTopY = yStartNewPage;
                float tableBottomY = margin;
                float[] columnWidths = {1.5f, 3f, 2f, 2f, 1.5f, 2f, 2f, 2f};

                float currentY = tableTopY;
                float currentX = margin;

                for (int i = 0; i < columnWidths.length; i++) {
                    String header = "";
                    switch (i) {
                        case 0:
                            header = "Product";
                            break;
                        case 1:
                            header = "Description";
                            break;
                        case 2:
                            header = "Price";
                            break;
                        case 3:
                            header = "Currency";
                            break;
                        case 4:
                            header = "Amount";
                            break;
                        case 5:
                            header = "Status";
                            break;
                        case 6:
                            header = "Total";
                            break;
                        case 7:
                            header = "";
                            break;
                    }
                    contentStream.beginText();
                    contentStream.newLineAtOffset(currentX, currentY);
                    contentStream.showText(header);
                    contentStream.endText();
                    currentX += columnWidths[i] * (tableWidth / 14);
                }

                // draw table content
                for (Invoice invoice : invoiceList) {
                    currentY -= rowHeight;
                    currentX = margin;
                    contentStream.beginText();
                    contentStream.newLineAtOffset(currentX, currentY);
                    contentStream.showText(invoice.getProduct());
                    contentStream.endText();
                    currentX += columnWidths[0] * (tableWidth / 14);

                    contentStream.beginText();
                    contentStream.newLineAtOffset(currentX, currentY);
                    contentStream.showText(invoice.getDescription());
                    contentStream.endText();
                    currentX += columnWidths[1] * (tableWidth / 14);

                    contentStream.beginText();
                    contentStream.newLineAtOffset(currentX, currentY);
                    contentStream.showText(Float.toString(invoice.getPrice()));
                    contentStream.endText();
                    currentX += columnWidths[2] * (tableWidth / 14);

                    contentStream.beginText();
                    contentStream.newLineAtOffset(currentX, currentY);
                    contentStream.showText(invoice.getCurrency().name());
                    contentStream.endText();
                    currentX += columnWidths[3] * (tableWidth / 14);

                    contentStream.beginText();
                    contentStream.newLineAtOffset(currentX, currentY);
                    contentStream.showText(Integer.toString(invoice.getAmount()));
                    contentStream.endText();
                    currentX += columnWidths[4] * (tableWidth / 14);

                    contentStream.beginText();
                    contentStream.newLineAtOffset(currentX, currentY);
                    contentStream.showText(invoice.getOrderCompleted() ? "Completed" : "Open");
                    contentStream.endText();
                    currentX += columnWidths[5] * (tableWidth / 14);

                    contentStream.beginText();
                    contentStream.newLineAtOffset(currentX, currentY);
                    contentStream.showText(Float.toString(invoice.getTotal()));
                    contentStream.endText();
                    currentX += columnWidths[6] * (tableWidth / 14);

                    // add a blank column at the end
                    currentX += columnWidths[7] * (tableWidth / 14);
                }
                // draw table footer
                currentY -= rowHeight;
                currentX = margin;
                contentStream.beginText();
                contentStream.newLineAtOffset(currentX, currentY);
                contentStream.showText("Total:");
                contentStream.endText();

                currentX += columnWidths[0] * (tableWidth / 14);

                contentStream.beginText();
                contentStream.newLineAtOffset(currentX, currentY);
                contentStream.showText("");
                contentStream.endText();

                currentX += columnWidths[1] * (tableWidth / 14);

                contentStream.beginText();
                contentStream.newLineAtOffset(currentX, currentY);
                contentStream.showText("");
                contentStream.endText();

                currentX += columnWidths[2] * (tableWidth / 14);

                contentStream.beginText();
                contentStream.newLineAtOffset(currentX, currentY);
                contentStream.showText("");
                contentStream.endText();

                currentX += columnWidths[3] * (tableWidth / 14);

                int totalAmount = 0;
                float totalValue = 0.0f;

                for (Invoice invoice : invoiceList) {
                    totalAmount += invoice.getAmount();
                    totalValue += invoice.getTotal();
                }

                contentStream.beginText();
                contentStream.newLineAtOffset(currentX, currentY);
                contentStream.showText(Integer.toString(totalAmount));
                contentStream.endText();

                currentX += columnWidths[4] * (tableWidth / 14);

                contentStream.beginText();
                contentStream.newLineAtOffset(currentX, currentY);
                contentStream.showText("");
                contentStream.endText();

                currentX += columnWidths[5] * (tableWidth / 14);

                contentStream.beginText();
                contentStream.newLineAtOffset(currentX, currentY);
                contentStream.showText(Float.toString(totalValue));
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
