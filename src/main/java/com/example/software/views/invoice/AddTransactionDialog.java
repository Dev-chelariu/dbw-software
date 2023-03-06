package com.example.software.views.invoice;

import com.example.software.data.entity.Product;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
@Slf4j
public class AddTransactionDialog extends Dialog {

    private Grid<Product> allProductsGrid;

    public AddTransactionDialog(List<Product> allProducts, Consumer<List<Product>> productConsumer) {
        super();
        this.allProductsGrid = createAllProductsGrid(allProducts);

        // Create the layout
        HorizontalLayout layout = new HorizontalLayout();
        layout.setWidthFull();
        layout.setSpacing(true);

        // Create the left side of the layout
        VerticalLayout leftSide = new VerticalLayout();
        leftSide.setSpacing(true);
        leftSide.add(new Label("All Products"));
        leftSide.add(allProductsGrid);
        layout.add(leftSide);

        Button addButton = new Button("Add");
        addButton.addClickListener(event -> {
            Set<Product> selectedProducts = allProductsGrid.getSelectedItems();
            List<Product> productList = new ArrayList<>(selectedProducts);
            productConsumer.accept(productList);
            close();
        });
        leftSide.add(addButton);

        // Set the content of the dialog to the layout
        this.add(layout);
        this.setWidth("800px");
        this.setHeight("600px");
        this.setHeaderTitle("Add Transaction");
    }

    private Grid<Product> createAllProductsGrid(List<Product> allProducts) {
        Grid<Product> grid = new Grid<>(Product.class);
        grid.setItems(allProducts);
        grid.setColumns("name", "price", "stockCount");
        grid.setSelectionMode(Grid.SelectionMode.MULTI);
        grid.addSelectionListener(event -> {
            Set<Product> selectedProducts = event.getAllSelectedItems();
            if (selectedProducts.isEmpty()) {
                log.error("No products selected!");
            }
        });
        return grid;
    }
}
