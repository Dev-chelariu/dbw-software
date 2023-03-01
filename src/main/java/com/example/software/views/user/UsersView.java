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
import com.vaadin.flow.data.binder.Binder;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@PageTitle("AddUser")
@Route(value = "users", layout = MainLayout.class)
@RouteAlias(value = "users", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@Uses(Icon.class)
public class UsersView extends VerticalLayout {

    MultiSelectComboBox<Role> rolesComboBox;

    public UsersView(UserService userService) {
        DefaultCrudFormFactory<User> formFactory = new DefaultCrudFormFactory<>(User.class) {


            @Override
            protected void configureForm(FormLayout formLayout, List<HasValueAndElement> fields) {
                Component nameField = (Component) fields.get(0);
                formLayout.setColspan(nameField, 3);

//                // Add an upload component for the profile picture
//                Upload upload = new Upload();
//                upload.setAcceptedFileTypes("image/jpeg", "image/png", "image/gif");
//                upload.setMaxFiles(1); // Allow only one file to be uploaded
//                formLayout.addFormItem(upload, "Profile Picture");

                // Add a ComboBox for selecting roles
                rolesComboBox = new MultiSelectComboBox<>();
                rolesComboBox.setItems(Role.values());
                rolesComboBox.setItemLabelGenerator(Role::getName);
                rolesComboBox.setLabel("Roles");

                // Add a listener to the component to retrieve selected values
                rolesComboBox.addValueChangeListener(event -> {
                    User newUser = new User();
                    Binder<User> binder = new Binder<>();
                    binder.setBean(newUser);
                    binder.bindInstanceFields(this);

                    Set<Role> selectedRoles = event.getValue();
                    newUser.setRoles(selectedRoles);

                    userService.register(newUser, selectedRoles);
                });

                formLayout.addFormItem(rolesComboBox," ");
            }
        };

        formFactory.setUseBeanValidation(true);
        formFactory.setVisibleProperties("username", "name", "hashedPassword, roles", "profilePicture");
        formFactory.setVisibleProperties(CrudOperation.ADD, "username", "name", "hashedPassword","roles", "profilePicture");

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
        Set<Role> roles = new HashSet<>();

        // Get the selected roles from the MultiSelectComboBox component
        Set<Role> selectedRoles = rolesComboBox.getValue();

        // Add the selected roles to the roles Set object
        for (Role role : selectedRoles) {
            roles.add(role);
        }

        return roles;
    }
}
