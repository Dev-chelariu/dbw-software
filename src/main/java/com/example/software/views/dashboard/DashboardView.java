package com.example.software.views.dashboard;


import com.example.software.data.entity.Customer;
import com.example.software.data.entity.ServiceHealth;
import com.example.software.data.entity.enums.Status;
import com.example.software.data.service.implementation.CustomerService;
import com.example.software.data.service.implementation.InvoiceService;
import com.example.software.data.service.implementation.ProductService;
import com.example.software.data.service.implementation.UserService;
import com.example.software.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.board.Board;
import com.vaadin.flow.component.charts.Chart;
import com.vaadin.flow.component.charts.model.*;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.theme.lumo.LumoUtility.*;

import javax.annotation.security.RolesAllowed;
import java.math.BigDecimal;
import java.util.List;

@PageTitle("Dashboard")
@Route(value = "dashboard", layout = MainLayout.class)
@RouteAlias(value = "dashboard", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class DashboardView extends Main {

    private final UserService userService;
    private final ProductService productService;
    private final InvoiceService invoiceService;

    private ServiceHealth serviceHealth = new ServiceHealth();

    private final CustomerService customerService;

    public DashboardView(UserService userService, ProductService productService, InvoiceService invoiceService, CustomerService customerService) {
        this.userService = userService;
        this.productService = productService;
        this.invoiceService = invoiceService;
        this.customerService = customerService;

        addClassName("dashboard-view");

         String value = String.valueOf(userService.count());
         String sProductsCount = String.valueOf(productService.count());
         String sInvoiceCount = String.valueOf(invoiceService.count());
         BigDecimal totalSale = invoiceService.getTotalSale();

        Board board = new Board();
        board.addRow(
               // createUserRow(),
                createHighlight("Current users", value, 12.2),
                createHighlight("Products", sProductsCount, 112.45),
                createHighlight("Invoices", sInvoiceCount, 33.9),
                createHighlight("Total sale in lei", totalSale.toString(), 1.0));
        board.addRow(createViewEvents());
        board.addRow(createServiceHealth(), createResponseTimes());
        add(board);
    }

    private Component createHighlight(String title, String value, Double percentage) {
        VaadinIcon icon = VaadinIcon.ARROW_UP;
        String prefix = "";
        String theme = "badge";

        if (percentage == 0) {
            prefix = "±";
        } else if (percentage > 0) {
            prefix = "+";
            theme += " success";
        } else if (percentage < 0) {
            icon = VaadinIcon.ARROW_DOWN;
            theme += " error";
        }

        H2 h2 = new H2(title);
        h2.addClassNames(FontWeight.NORMAL, Margin.NONE, TextColor.SECONDARY, FontSize.XSMALL);

        Span span = new Span(value);
        span.addClassNames(FontWeight.SEMIBOLD, FontSize.XXXLARGE);

        Icon i = icon.create();
        i.addClassNames(BoxSizing.BORDER, Padding.XSMALL);

        Span badge = new Span(i, new Span(prefix + percentage.toString()));
        badge.getElement().getThemeList().add(theme);

        VerticalLayout layout = new VerticalLayout(h2, span, badge);
        layout.addClassName(Padding.LARGE);
        layout.setPadding(false);
        layout.setSpacing(false);
        return layout;
    }


    private Component createViewEvents() {
        // Header
        Select year = new Select();
        year.setItems("2018", "2019", "2020", "2021","2022","2023");
        year.setValue("2023");
        year.setWidth("100px");

        HorizontalLayout header = createHeader("Sales Warehouse", "City/month");
        header.add(year);

        // Chart
        Chart chart = new Chart(ChartType.AREASPLINE);
        Configuration conf = chart.getConfiguration();
        conf.getChart().setStyledMode(true);

        XAxis xAxis = new XAxis();
        xAxis.setCategories("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec");
        conf.addxAxis(xAxis);

        conf.getyAxis().setTitle("Values");

        PlotOptionsAreaspline plotOptions = new PlotOptionsAreaspline();
        plotOptions.setPointPlacement(PointPlacement.ON);
        plotOptions.setMarker(new Marker(false));
        conf.addPlotOptions(plotOptions);

        conf.addSeries(new ListSeries("Iasi", 189, 191, 291, 396, 501, 403, 609, 712, 729, 942, 1044, 1247));
        conf.addSeries(new ListSeries("Neamt", 138, 246, 248, 348, 352, 353, 463, 573, 778, 779, 885, 887));
        conf.addSeries(new ListSeries("Suceava", 65, 65, 166, 171, 293, 302, 308, 317, 427, 429, 535, 636));
        conf.addSeries(new ListSeries("Botosani", 0, 11, 17, 123, 130, 142, 248, 349, 452, 454, 458, 462));

        // Add it all together
        VerticalLayout viewEvents = new VerticalLayout(header, chart);
        viewEvents.addClassName(Padding.LARGE);
        viewEvents.setPadding(false);
        viewEvents.setSpacing(false);
        viewEvents.getElement().getThemeList().add("spacing-l");
        return viewEvents;
    }

    private Component createServiceHealth() {
        // Header
        HorizontalLayout header = createHeader("Service health", "Input / output");

        // Grid
        Grid<ServiceHealth> grid = new Grid();
        grid.addThemeVariants(GridVariant.LUMO_NO_BORDER);
        grid.setAllRowsVisible(true);

        grid.addColumn(new ComponentRenderer<>(serviceHealth -> {
            Span status = new Span();
            String statusText = getStatusDisplayName(serviceHealth);
            status.getElement().setAttribute("aria-label", "Status: " + statusText);
            status.getElement().setAttribute("title", "Status: " + statusText);
            status.getElement().getThemeList().add(getStatusTheme(serviceHealth));
            return status;
        })).setHeader("").setFlexGrow(0).setAutoWidth(true);
        grid.addColumn(ServiceHealth::getService).setHeader("Services").setFlexGrow(1);
        grid.addColumn(ServiceHealth::getInput).setHeader("Input").setAutoWidth(true).setTextAlign(ColumnTextAlign.END);
        grid.addColumn(ServiceHealth::getOutput).setHeader("Output").setAutoWidth(true)
                .setTextAlign(ColumnTextAlign.END);

        grid.setItems(new ServiceHealth(Status.EXCELLENT, "Invoices", 324, 1540),
                new ServiceHealth(Status.OK, "Email", 453, 1350),
                new ServiceHealth(Status.OK, "User", 31, 10),
                new ServiceHealth(Status.OK, "Employee", 31, 10),
                new ServiceHealth(Status.OK, "Product", 3117, 130),
                new ServiceHealth(Status.OK, "Spreadsheet", 11, 10),
                new ServiceHealth(Status.EXCELLENT, "List Invoices", 30, 19),
                new ServiceHealth(Status.FAILING, "No service falling", 0, 0));


        // Add it all together
        VerticalLayout serviceHealth = new VerticalLayout(header, grid);
        serviceHealth.addClassName(Padding.LARGE);
        serviceHealth.setPadding(false);
        serviceHealth.setSpacing(false);
        serviceHealth.getElement().getThemeList().add("spacing-l");
        return serviceHealth;
    }
//Charts
    private Component createResponseTimes() {
        HorizontalLayout header = createHeader("Top Customers", "Displays loyal customers");

        // Chart
        Chart chart = new Chart(ChartType.PIE);
        Configuration conf = chart.getConfiguration();
        conf.setTitle("Customers with the Most Invoices");
        conf.getLegend().setEnabled(false);
        conf.getChart().setStyledMode(true);
        chart.setThemeName("gradient");
        // Get the top 10 customers with the most invoices
        List<Customer> topCustomers = customerService.getCustomersByInvoiceCount(5);

        DataSeries series = new DataSeries();

        for (Customer customer : topCustomers) {
            series.add(new DataSeriesItem(customer.getFirstName(), customer.getInvoiceCount()));
        }
        conf.setSeries(series);

        // Add it all together
        VerticalLayout serviceHealth = new VerticalLayout(header, chart);
        serviceHealth.addClassName(Padding.LARGE);
        serviceHealth.setPadding(false);
        serviceHealth.setSpacing(false);
        serviceHealth.getElement().getThemeList().add("spacing-l");
        return serviceHealth;
    }

    private HorizontalLayout createHeader(String title, String subtitle) {
        H2 h2 = new H2(title);
        h2.addClassNames(FontSize.XLARGE, Margin.NONE);

        Span span = new Span(subtitle);
        span.addClassNames(TextColor.SECONDARY, FontSize.XSMALL);

        VerticalLayout column = new VerticalLayout(h2, span);
        column.setPadding(false);
        column.setSpacing(false);

        HorizontalLayout header = new HorizontalLayout(column);
        header.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        header.setSpacing(false);
        header.setWidthFull();
        return header;
    }

    private String getStatusDisplayName(ServiceHealth serviceHealth) {
        Status status = serviceHealth.getStatus();
        if (status == Status.OK) {
            return "Ok";
        } else if (status == Status.FAILING) {
            return "Failing";
        } else if (status == Status.EXCELLENT) {
            return "Excellent";
        } else {
            return status.toString();
        }
    }

    private String getStatusTheme(ServiceHealth serviceHealth) {
        Status status = serviceHealth.getStatus();
        String theme = "badge primary small";
        if (status == Status.EXCELLENT) {
            theme += " success";
        } else if (status == Status.FAILING) {
            theme += " error";
        }
        return theme;
    }

}
