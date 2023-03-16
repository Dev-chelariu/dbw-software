package com.example.software.views;

import com.example.software.components.appnav.AppNav;
import com.example.software.components.appnav.AppNavItem;
import com.example.software.data.entity.User;
import com.example.software.security.AuthenticatedUser;
import com.example.software.views.dashboard.DashboardView;
import com.example.software.views.email.EmailView;
import com.example.software.views.employee.EmployeesView;
import com.example.software.views.invoice.InvoiceListView;
import com.example.software.views.invoice.InvoiceView;
import com.example.software.views.map.MapView;
import com.example.software.views.product.ProductsView;
import com.example.software.views.spreadsheet.SpreadsheetView;
import com.example.software.views.user.UsersView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.dom.ThemeList;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.theme.lumo.Lumo;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.io.ByteArrayInputStream;
import java.util.Optional;

/**
 * The main view is a top-level placeholder for other views.
 */

public class MainLayout extends AppLayout {

    private H2 viewTitle;

    private AuthenticatedUser authenticatedUser;
    private AccessAnnotationChecker accessChecker;

    public MainLayout(AuthenticatedUser authenticatedUser, AccessAnnotationChecker accessChecker) {
        this.authenticatedUser = authenticatedUser;
        this.accessChecker = accessChecker;

        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.getElement().setAttribute("aria-label", "Menu toggle");

        viewTitle = new H2();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        addToNavbar(true, toggle, viewTitle);
    }

    private void addDrawerContent() {
        H1 appName = new H1();
        appName.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        Header header = new Header(appName);
        // Create the logo image object
        Image logoImage = new Image("images/logo.png", "DBW Software Logo");
        logoImage.setAlt("DBW Software Logo");
        logoImage.setHeight("200px");
        logoImage.setWidth("200px");

        header.add(logoImage);

        Button toggleButton = new Button("Dark Theme", click -> {
            ThemeList themeList = UI.getCurrent().getElement().getThemeList();
            if(themeList.contains(Lumo.DARK)) {
                themeList.remove(Lumo.DARK);
            } else {
                themeList.add(Lumo.DARK);
            }
        });

        Scroller scroller = new Scroller(createNavigation());

        addToDrawer(header, scroller, createFooter(), toggleButton);
    }

    private AppNav createNavigation() {
        // AppNav is not yet an official component.
        // For documentation, visit https://github.com/vaadin/vcf-nav#readme
        AppNav nav = new AppNav();

        if (accessChecker.hasAccess(DashboardView.class)) {
            nav.addItem(new AppNavItem("Dashboard", DashboardView.class, "la la-chart-area"));
        }
        if (accessChecker.hasAccess(UsersView.class)) {
            nav.addItem(new AppNavItem("Users", UsersView.class, "la la-user"));
        }
        if (accessChecker.hasAccess(EmailView.class)) {
            nav.addItem(new AppNavItem("Email", EmailView.class, "la la-mail-bulk"));
        }
        if (accessChecker.hasAccess (EmployeesView.class)) {
            nav.addItem (new AppNavItem ("Employee", EmployeesView.class, "la la-user"));
        }
        if (accessChecker.hasAccess (ProductsView.class)) {
            nav.addItem (new AppNavItem ("Product", ProductsView.class, "la la-columns"));
        }
        if (accessChecker.hasAccess(SpreadsheetView.class)) {
            nav.addItem(new AppNavItem("Spreadsheet", SpreadsheetView.class, "la la-file-excel"));
        }
        if (accessChecker.hasAccess(InvoiceView.class)) {
            nav.addItem(new AppNavItem("Invoice", InvoiceView.class, "lab la-dochub"));
        }
        if (accessChecker.hasAccess(InvoiceListView.class)) {
            nav.addItem(new AppNavItem("List Invoices", InvoiceListView.class, "lab la-dochub"));
        }
        if (accessChecker.hasAccess(MapView.class)) {
            nav.addItem(new AppNavItem("Location", MapView.class, "la la-map"));
        }
        return nav;
    }

    private Footer createFooter() {
        Footer layout = new Footer();

        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();

            Avatar avatar = new Avatar(user.getName());
            StreamResource resource = new StreamResource("profile-pic",
                    () -> new ByteArrayInputStream(user.getProfilePicture()));
            avatar.setImageResource(resource);
            avatar.setThemeName("xsmall");
            avatar.getElement().setAttribute("tabindex", "-1");

            MenuBar userMenu = new MenuBar();
            userMenu.setThemeName("tertiary-inline contrast");

            MenuItem userName = userMenu.addItem("");
            Div div = new Div();
            div.add(avatar);
            div.add(user.getName());
            div.add(new Icon("lumo", "dropdown"));
            div.getElement().getStyle().set("display", "flex");
            div.getElement().getStyle().set("align-items", "center");
            div.getElement().getStyle().set("gap", "var(--lumo-space-s)");
            userName.add(div);
            userName.getSubMenu().addItem("Sign out", e -> {
                authenticatedUser.logout();
            });

            layout.add(userMenu);
        } else {
            Anchor loginLink = new Anchor("login", "Sign in");
            layout.add(loginLink);
        }

        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}
