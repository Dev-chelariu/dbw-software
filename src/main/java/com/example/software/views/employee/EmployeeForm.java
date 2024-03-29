package com.example.software.views.employee;

import com.example.software.data.entity.dto.EmployeeDTO;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.shared.Registration;

public class EmployeeForm extends FormLayout {

    private EmployeeDTO employee;

    TextField firstName = new TextField("First name");
    TextField lastName = new TextField("Last name");
    EmailField email = new EmailField("Email");
    TextField phone = new TextField("Phone");
    DatePicker dateOfBirth = new DatePicker ("DateOfBirth");
    TextField occupation = new TextField("Occupation");
    Binder<EmployeeDTO> binder = new BeanValidationBinder<> (EmployeeDTO.class);

    Button save = new Button("Save");
    Button delete = new Button("Delete");
    Button close = new Button("Cancel");

    public EmployeeForm() {
        addClassName ("person-form");
        binder.bindInstanceFields (this);

        add(
                firstName,
                lastName,
                email,
                phone,
                dateOfBirth,
                occupation,
                createButtonLayout());
    }

    private HorizontalLayout createButtonLayout() {
        save.addThemeVariants (ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants (ButtonVariant.LUMO_ERROR);
        close.addThemeVariants (ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut (Key.ENTER);
        close.addClickShortcut (Key.ESCAPE);

        save.addClickListener (event-> validateAndSave());
        delete.addClickListener (event -> fireEvent (new DeleteEvent (this, employee)));
        close.addClickListener (even -> fireEvent (new CloseEvent (this)));

        binder.addStatusChangeListener(e -> save.setEnabled(binder.isValid()));

        return new HorizontalLayout (save,delete,close);
    }

    public void setEmployee(EmployeeDTO employee){
        this.employee = employee;
        binder.readBean(employee);
    }

    private void validateAndSave() {
        try {
            binder.writeBean (employee);
            fireEvent (new SaveEvent (this, employee));
        } catch (ValidationException e) {
            e.printStackTrace ();
        }
    }

    // Events
    public static abstract class EmployeeFormEvent extends ComponentEvent<EmployeeForm> {

        private final EmployeeDTO employee;

        protected EmployeeFormEvent(EmployeeForm source, EmployeeDTO employee) {
            super(source, false);
            this.employee = employee;
        }

        public EmployeeDTO getEmployee() {
            return employee;
        }
    }

    public static class SaveEvent extends EmployeeFormEvent {
        SaveEvent(EmployeeForm source, EmployeeDTO employee) {
            super(source, employee);
        }
    }

    public static class DeleteEvent extends EmployeeFormEvent {
        DeleteEvent(EmployeeForm source, EmployeeDTO employee) {
            super(source, employee);
        }

    }

    public static class CloseEvent extends EmployeeFormEvent {
        CloseEvent(EmployeeForm source) {
            super(source, null);
        }
    }

    public <T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                  ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}
