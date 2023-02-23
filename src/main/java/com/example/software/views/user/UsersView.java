package com.example.software.views.user;

import com.example.software.data.entity.User;
import com.example.software.data.service.implementation.UserService;
import com.example.software.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValueAndElement;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.StreamResource;
import org.vaadin.crudui.crud.CrudOperation;
import org.vaadin.crudui.crud.impl.GridCrud;
import org.vaadin.crudui.form.impl.form.factory.DefaultCrudFormFactory;
import org.vaadin.crudui.layout.impl.HorizontalSplitCrudLayout;
import org.vaadin.haijian.Exporter;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@PageTitle("AddUser")
@Route(value = "users", layout = MainLayout.class)
@RouteAlias(value = "users", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@Uses(Icon.class)
public class UsersView extends VerticalLayout {

    public UsersView(UserService userService) {

        DefaultCrudFormFactory<User> formFactory = new DefaultCrudFormFactory<> (User.class) {

            @Override
            protected void configureForm(FormLayout formLayout, List<HasValueAndElement> fields) {
                Component nameField = (Component) fields.get (0);
                formLayout.setColspan (nameField, 2);
            }
        };
        formFactory.setUseBeanValidation(true);
        formFactory.setVisibleProperties(
                "username", "name", "hashedPassword","roles", "profilePicture");

//        ComboBox<String> roles = new ComboBox<>();
//        roles.getElement().setAttribute("colspan", "2");
//        roles.setLabel("Roles");

//        MultiSelectComboBox<Role> roles = new MultiSelectComboBox<> ();
//        roles.setItems (Role.values ());
//
//        Binder<User> user = new Binder<>();
//        user.bind (roles, User::getRoles, User::setRoles);

        formFactory.setVisibleProperties(
                CrudOperation.ADD,
                "username", "name", "hashedPassword", "roles", "profilePicture");

        // crud instance
        GridCrud<User> crud = new GridCrud<> (User.class, new HorizontalSplitCrudLayout (), formFactory);
        crud.setClickRowToUpdate (true);
        crud.setUpdateOperationVisible (false);

        // grid configuration
        crud.getGrid ().setColumns ("username", "name", "hashedPassword", "roles","profilePicture");

        crud.getGrid ().setColumnReorderingAllowed (true);

        // layout configuration
        setSizeFull ();
        Anchor anchorXLSX = new Anchor (new StreamResource ("Users.xlsx",
                Exporter.exportAsExcel (crud.getGrid ())), "Download Excel");
        anchorXLSX.getElement ()
                  .setAttribute ("download", true);

        add (crud, anchorXLSX);
        // logic configuration
        crud.setOperations(
                userService::findAll,
                userService::register,
                userService::register,
                userService::delete
            );
    }
}
