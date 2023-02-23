package com.example.software.views.invoice;

import com.example.software.views.MainLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;

import javax.annotation.security.PermitAll;

@PageTitle("Invoice")
@Route(value = "invoice", layout = MainLayout.class)
@RouteAlias(value = "invoice", layout = MainLayout.class)
@PermitAll
public class InvoiceView extends VerticalLayout {

    public InvoiceView() {
    }
}
