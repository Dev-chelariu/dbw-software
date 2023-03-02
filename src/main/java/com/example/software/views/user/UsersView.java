package com.example.software.views.user;

import com.example.software.data.entity.User;
import com.example.software.data.entity.enums.Role;
import com.example.software.data.service.implementation.UserService;
import com.example.software.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasValueAndElement;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.vaadin.flow.server.StreamResource;
import lombok.extern.slf4j.Slf4j;
import org.vaadin.crudui.crud.CrudOperation;
import org.vaadin.crudui.crud.impl.GridCrud;
import org.vaadin.crudui.form.impl.form.factory.DefaultCrudFormFactory;
import org.vaadin.crudui.layout.impl.HorizontalSplitCrudLayout;
import org.vaadin.haijian.Exporter;

import javax.annotation.security.RolesAllowed;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@PageTitle("AddUser")
@Route(value = "users", layout = MainLayout.class)
@RouteAlias(value = "users", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@Uses(Icon.class)
@Slf4j
public class UsersView extends VerticalLayout {

    MultiSelectComboBox<Role> roles = new MultiSelectComboBox<>();

    public UsersView(UserService userService) {

        DefaultCrudFormFactory<User> formFactory = new DefaultCrudFormFactory<>(User.class) {
            //ORDER: ADD FIRST ROLE AFTER OTHERS, BECAUSE OTHERWISE WILL DELETE, ALL

            @Override
            protected void configureForm(FormLayout formLayout, List<HasValueAndElement> fields) {
                Component nameField = (Component) fields.get(0);
                formLayout.setColspan(nameField, 4);

//                // Add an upload component for the profile picture
                Upload upload = new Upload();
                upload.setAcceptedFileTypes("image/jpeg", "image/png", "image/gif");
                upload.setMaxFiles(1); // Allow only one file to be uploaded
                formLayout.addFormItem(upload, "");

                roles.setItems(Role.values());
                roles.setItemLabelGenerator(Role::getName);
                roles.setLabel("Roles");

                // Add a listener to the component to retrieve selected values
                roles.addValueChangeListener(event ->  {
                    try {
                        User newUser = new User();
                        binder.setBean(newUser);
                        binder.bindInstanceFields(this);
                        Set<Role> roless = event.getValue();
                        newUser.setRoles(roless);
                    } catch (Throwable e) {
                        log.error("We have troubles", e);
                    }
                });
                formLayout.addFormItem(roles,"");
            }
        };

        formFactory.setUseBeanValidation(true);
        formFactory.setVisibleProperties("roles","username", "name", "hashedPassword","profilePicture");
        formFactory.setVisibleProperties(CrudOperation.ADD,"roles", "username", "name", "hashedPassword","profilePicture");

        // crud instance
        GridCrud<User> crud = new GridCrud<>(User.class, new HorizontalSplitCrudLayout(), formFactory);
        crud.setClickRowToUpdate(true);
        crud.setUpdateOperationVisible(false);

        // grid configuration
        crud.getGrid().setColumns("username", "name", "roles");
        crud.getGrid().addColumn(new AvatarRenderer()).setHeader("Avatar")
                .setAutoWidth(true).setFlexGrow(1);
        crud.getGrid().addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);

        // layout configuration
        setSizeFull();

        Anchor anchorXLSX = new Anchor(new StreamResource("Users.xlsx",
                Exporter.exportAsExcel(crud.getGrid())), "Download Excel");
        anchorXLSX.getElement().setAttribute("download", true);

        add(crud, anchorXLSX);

        // logic configuration
        crud.setOperations(
                userService::findAll,
                user -> {
                    Set<Role> roles = getUserRolesFromForm();
                    userService.register(user, roles);
                    return user;
                },
                user -> {
                    Set<Role> roles = getUserRolesFromForm();
                    userService.register(user, roles);
                    return user;
                },
                userService::delete
        );
    }

    private Set<Role> getUserRolesFromForm() {
        Set<Role> sRoles = new HashSet<>();
        // Get the selected roles from the MultiSelectComboBox component
        Set<Role> selectedRoles = roles.getValue();

        // Add the selected roles to the roles Set object
        for (Role role : selectedRoles) {
            sRoles.add(role);
        }
        return sRoles;
    }
}