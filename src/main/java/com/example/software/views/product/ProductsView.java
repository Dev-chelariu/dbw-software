package com.example.software.views.product;

import com.example.software.data.entity.dto.ProductDTO;
import com.example.software.data.service.IProduct;
import com.example.software.views.MainLayout;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import org.vaadin.crudui.crud.CrudOperation;
import org.vaadin.crudui.crud.impl.GridCrud;
import org.vaadin.haijian.Exporter;

import javax.annotation.security.PermitAll;
import java.text.DecimalFormat;
import java.util.Comparator;

@PageTitle("Product")
@Route(value = "product", layout = MainLayout.class)
@PermitAll
@Uses(Icon.class)
public class ProductsView extends VerticalLayout implements BeforeEnterObserver {

    public ProductsView(IProduct iProduct) {

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
        //
       // HorizontalLayout toolbar = new HorizontalLayout (filter, );
        crud.getCrudLayout().addFilterComponents(filter, anchorXLSX, anchorCSV);

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

// Add an traffic light icon in front of availability
        // Three css classes with the same names of three availability values,
        // Available, Coming and Discontinued, are defined in shared-styles.css
        // and are
        // used here in availabilityTemplate.
//        final String availabilityTemplate = "<iron-icon icon=\"vaadin:circle\" class-name=\"[[item.availability]]\"></iron-icon> [[item.availability]]";
//        crud.getGrid ().addColumn ((TemplateRenderer.<ProductDTO>of(availabilityTemplate)
//                                                                    .withProperty("availability",
//                                          product -> product.getAvailability().toString())))
//                .setHeader("Availability")
//                .setComparator(Comparator
//                        .comparing(ProductDTO::getAvailability))
//                .setTextAlign (ColumnTextAlign.END)
//                .setFlexGrow(1).setKey("availability");

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
                () -> iProduct.findByNameContainingIgnoreCase(filter.getValue()),
                iProduct::addProduct,
                iProduct::update,
                iProduct::delete);

        filter.addValueChangeListener(e -> crud.refreshGrid());
    }

    @Override
    public void beforeEnter(BeforeEnterEvent beforeEnterEvent) {

    }
}
