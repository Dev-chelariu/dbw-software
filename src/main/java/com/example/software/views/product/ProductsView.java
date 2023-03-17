package com.example.software.views.product;

import com.example.software.data.entity.Product;
import com.example.software.data.entity.dto.ProductDTO;
import com.example.software.data.service.implementation.ProductService;
import com.example.software.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import org.vaadin.crudui.crud.CrudOperation;
import org.vaadin.crudui.crud.impl.GridCrud;
import org.vaadin.haijian.Exporter;

import javax.annotation.security.PermitAll;
import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@PageTitle("Product")
@Route(value = "product", layout = MainLayout.class)
@PermitAll
@Uses(Icon.class)
public class ProductsView extends VerticalLayout {

    private final ProductService productService;

    public ProductsView(ProductService productService) {
        this.productService = productService;

        GridCrud<ProductDTO> crud = new GridCrud<> (ProductDTO.class);

        // additional components
        TextField filter = new TextField();
        filter.setPlaceholder("Filter by name");
        filter.setClearButtonVisible(true);

        Anchor anchorXLSX = new Anchor (new StreamResource ("Products.xlsx",
                Exporter.exportAsExcel (crud.getGrid ())), "Download As XLSX");
        anchorXLSX.getElement ()
                  .setAttribute ("download", true);

        Anchor anchorCSV = new Anchor (new StreamResource ("Products.csv",
                Exporter.exportAsCSV (crud.getGrid ())), "Download As CSV");
        anchorCSV.getElement ()
                 .setAttribute ("download", true);

        Button lowQuantityButton = new Button("Show Low Quantity Products", new Icon(VaadinIcon.CHECK_SQUARE));

        lowQuantityButton.addClickListener(event -> handleLowQuantityProducts());

        crud.getCrudLayout().addFilterComponents(filter, anchorXLSX, anchorCSV, lowQuantityButton);

        final DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setMaximumFractionDigits(2);
        decimalFormat.setMinimumFractionDigits(2);

        // grid configuration
        crud.getGrid().setColumns("codProduct", "name", "category", "description", "unitMeasure","availability");
        crud.getGrid().setColumnReorderingAllowed(true);

        //PRICE configuration
        crud.getGrid ().addColumn (product -> decimalFormat.format(product.getPrice()) + " €")
                .setHeader("Price")
                .setComparator(Comparator.comparing(ProductDTO::getPrice))
                .setFlexGrow(0).setKey("price");

        //stock config
        crud.getGrid ().addColumn(product -> product.getStockCount() == 0 ? "-"
                : Integer.toString(product.getStockCount()))
                .setHeader("Stock count")
                .setComparator(
                        Comparator.comparingInt(ProductDTO::getStockCount))
                .setTextAlign (ColumnTextAlign.CENTER)
                .setFlexGrow(1).setKey("stock");

        // form configuration
       crud.getCrudFormFactory ()
           .setUseBeanValidation (true);
        crud.getCrudFormFactory ()
            .setVisibleProperties (
                    "codProduct", "name", "category", "description", "price", "unitMeasure", "stockCount", "availability");
        crud.getCrudFormFactory ()
            .setVisibleProperties (
                    CrudOperation.ADD
                    , "name", "category", "description", "price", "unitMeasure", "stockCount", "availability");

        add (crud);
        setSizeFull ();

        crud.setOperations(
                () -> productService.findByNameContainingIgnoreCase(filter.getValue()),
                productService::addProduct,
                productService::update,
                productService::delete);

        filter.addValueChangeListener(e -> crud.refreshGrid());
    }

    private void handleLowQuantityProducts() {

            Dialog dialog = new Dialog();
            dialog.setCloseOnEsc(false);
            dialog.setCloseOnOutsideClick(false);

        AtomicBoolean isDialogOpen = new AtomicBoolean(true);

        // Adaugă un ascultător de evenimente care este declanșat când dialogul este închis
        dialog.addDialogCloseActionListener(event -> {
            isDialogOpen.set(false);
        });

        List<Product> lowStockProducts = productService.getLowQuantityProducts();
        if (lowStockProducts.isEmpty()) {
            Notification.show("No low stock products found");
        } else {
            Grid<Product> grid = new Grid<>();
            H3 title = new H3("Prepare a new order for those products");

            grid.setItems(lowStockProducts);
            grid.addColumn(Product::getName).setHeader("Product");
            grid.addColumn(new ComponentRenderer<>(product -> {
                Span stockSpan = new Span(String.valueOf(product.getStockCount()));
                if (product.getStockCount() < 10) {
                    stockSpan.getStyle().set("color", "red");
                }
                return stockSpan;
            })).setHeader("Stock");

            grid.setWidth("500px");
            dialog.add(title, grid);

            // Afișează notificarea și oprește afișarea notificării atunci când dialogul este închis

            Notification notification = new Notification("Found " + lowStockProducts.size() + " low stock products", 5000, Notification.Position.BOTTOM_END);
            notification.open();
        }
        // Add buttons to dialog
        Button closeButton = new Button("Close", e -> dialog.close());
        dialog.add(closeButton);

        // Open dialog
        dialog.open();
    }
}
