package com.example.software.views.employee;

import com.example.software.data.entity.dto.EmployeeDTO;
import com.example.software.data.service.IPerson;
import com.example.software.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import org.vaadin.haijian.Exporter;

import javax.annotation.security.RolesAllowed;

@PageTitle("Employee")
@Route(value = "employee", layout = MainLayout.class)
@RolesAllowed("ADMIN")
@Uses(Icon.class)
public class EmployeesView extends VerticalLayout {

    Grid<EmployeeDTO> grid = new Grid<> (EmployeeDTO.class);
    TextField filterText = new TextField();

    private final IPerson employeeService;
    EmployeeForm form;

    public EmployeesView(IPerson employeeService) {
        this.employeeService = employeeService;
        addClassNames("employee-view");
        setSizeFull();
        configureGrid ();

        form = new EmployeeForm ();
        form.setWidth ("25em");

        form.addListener (EmployeeForm.SaveEvent.class, this::saveEmployee);
        form.addListener (EmployeeForm.DeleteEvent.class, this::deleteEmployee);
        form.addListener (EmployeeForm.CloseEvent.class, e-> closeEditor ());

       // configureForm();
        FlexLayout content = new FlexLayout(grid, form);
        content.setFlexGrow(2, grid);
        content.setFlexGrow(1, form);
        content.setFlexShrink(0, form);
        content.addClassNames("content", "gap-m");
        content.setSizeFull();

        add(
                getToolbar(),
                content
        );

        updateList();
        closeEditor ();
        grid.asSingleSelect().addValueChangeListener(event ->
                editEmployee (event.getValue()));
    }

    private void updateList() {
        grid.setItems (employeeService.findAll (filterText.getValue ()));
    }

    private void deleteEmployee(EmployeeForm.DeleteEvent event) {
        employeeService.delete (event.getEmployee ());
        updateList ();
        closeEditor ();
    }

    private void saveEmployee(EmployeeForm.SaveEvent event) {
        employeeService.save (event.getEmployee ());
        updateList ();
        closeEditor ();
    }

    private HorizontalLayout getToolbar() {
        filterText.setPlaceholder ("Filter by name...");
        filterText.setClearButtonVisible (true);
        Anchor anchorXLSX = new Anchor (new StreamResource ("Employees.xlsx",
                Exporter.exportAsExcel (grid)), "Download As XLSX");
        anchorXLSX.getElement ()
                  .setAttribute ("download", true);

        Anchor anchorCSV = new Anchor (new StreamResource ("Employees.csv",
                Exporter.exportAsCSV (grid)), "Download As CSV");
        anchorCSV.getElement ()
                 .setAttribute ("download", true);

        filterText.setValueChangeMode (ValueChangeMode.LAZY);
        filterText.addValueChangeListener (e-> updateList ());

        Button addEmployeeBtn = new Button ("Add Employee");
        addEmployeeBtn.addClickListener (e -> addEmployee ());

        HorizontalLayout toolbar = new HorizontalLayout (filterText, addEmployeeBtn, anchorXLSX, anchorCSV);
        toolbar.addClassName ("toolbar");
        return toolbar;
    }

    private void addEmployee() {
        grid.asSingleSelect ().clear ();
        editEmployee (new EmployeeDTO ());
    }

    private void editEmployee(EmployeeDTO employee) {
        if (employee == null) {
            closeEditor ();
        } else {
            form.setEmployee (employee);
            form.setVisible (true);
            addClassName ("editing");
        }
    }

    private void closeEditor() {
        form.setEmployee (null);
        form.setVisible (false);
        removeClassName ("editing");
    }

    private void configureGrid() {
        grid.addClassName ("employee-grid");
        grid.setSizeFull ();
        grid.setColumns ("firstName", "lastName", "email", "phone", "dateOfBirth", "occupation");
        grid.getColumns ().forEach(col ->col.setAutoWidth(true));
    }
}
