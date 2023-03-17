package com.example.software.views.invoice;

import com.example.software.data.entity.Invoice;
import com.example.software.data.service.implementation.InvoiceService;
import com.example.software.views.MainLayout;
import com.example.software.views.invoice.ExportUtils.PdfUtils;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.StreamResource;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.security.PermitAll;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@PageTitle("Invoice - List")
@Route(value = "invoice/list", layout = MainLayout.class)
@RouteAlias(value = "list", layout = MainLayout.class)
@PermitAll
public class InvoiceListView extends VerticalLayout {

    private final InvoiceService invoiceService;

    private PdfUtils pdfUtils = new PdfUtils();

    public InvoiceListView(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;

        Grid<Invoice> grid = new Grid<>(Invoice.class, false);

        // Filter by invoice name
        TextField invoiceNameFilter = new TextField("Filter by invoice name");
        invoiceNameFilter.addValueChangeListener(e -> {
            String filterText = e.getValue();
            ListDataProvider<Invoice> dataProvider = (ListDataProvider<Invoice>) grid.getDataProvider();
            dataProvider.setFilter(Invoice::getName,
                    name -> StringUtils.containsIgnoreCase(name, filterText));
        });

        // Filter by invoice date
        DatePicker invoiceDateFilter = new DatePicker("Filter by invoice date");
        invoiceDateFilter.addValueChangeListener(e -> {
            LocalDate filterDate = e.getValue();
            ListDataProvider<Invoice> dataProvider = (ListDataProvider<Invoice>) grid.getDataProvider();
            dataProvider.setFilter(Invoice::getInvoiceDate,
                    date -> date.equals(filterDate));
        });

        invoiceNameFilter.setClearButtonVisible (true);
        invoiceDateFilter.setClearButtonVisible(true);

        HorizontalLayout filterLayout = new HorizontalLayout(invoiceNameFilter, invoiceDateFilter);
        filterLayout.setSpacing(true);

        grid.addColumn(Invoice::getName).setHeader("Invoice Name");
        grid.addColumn(Invoice::getInvoiceDate).setHeader("Invoice Date");
        grid.addColumn(Invoice::getTotal).setHeader("Invoice Total");
        grid.addColumn(createToggleDetailsRenderer(grid)).setHeader("Details");

        grid.addComponentColumn(invoice -> {
            Button button = new Button(new Icon(VaadinIcon.DOWNLOAD));
            button.addClickListener(event ->

                    downloadPdf(invoice));
            return button;
        }).setHeader("Download PDF");


        grid.setDetailsVisibleOnClick(false);
        grid.setItemDetailsRenderer(createPersonDetailsRenderer());

        List<Invoice> invoices = invoiceService.findAll();
        grid.setItems(invoices);
        grid.setHeight("720px");
        grid.addThemeVariants(GridVariant.LUMO_COLUMN_BORDERS);

        add(filterLayout, grid);
    }

    private static Renderer<Invoice> createToggleDetailsRenderer(
            Grid<Invoice> grid) {
        return LitRenderer.<Invoice> of(
                        "<vaadin-button theme=\"tertiary\" @click=\"${handleClick}\">More</vaadin-button>")
                .withFunction("handleClick",
                        invoice -> grid.setDetailsVisible(invoice,
                                !grid.isDetailsVisible(invoice)));
    }

    private static ComponentRenderer<InvoiceDetailsForm, Invoice> createPersonDetailsRenderer() {
        return new ComponentRenderer<>(InvoiceDetailsForm::new,
                InvoiceDetailsForm::setInvoice);
    }

    private void downloadPdf(Invoice invoice) {
        // Get the invoice from the database
        Invoice dbInvoice = invoiceService.getInvoiceById(invoice.getInvoiceId());

        // Generate the PDF and store it in a byte array
        try {
            byte[] pdfBytes = pdfUtils.createPdf(dbInvoice);

            // Create a stream resource from the PDF bytes
            StreamResource resource = new StreamResource(invoice.getName() + "i.pdf", () ->
                    new ByteArrayInputStream(pdfBytes));

            // Add the download link to the UI
            Anchor downloadLink = new Anchor(resource, "Your Invoice is Here  ");
            downloadLink.getElement().setAttribute("download", true);

            downloadLink.add(new Icon(VaadinIcon.DOWNLOAD_ALT));
            Notification.show("Your invoice is ready in the footer of the page!");
            add(downloadLink);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
