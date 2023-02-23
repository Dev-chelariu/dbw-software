package com.example.software.views.email;

import com.example.software.data.entity.Email;
import com.example.software.data.service.IMail;
import com.example.software.views.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import javax.annotation.security.PermitAll;
import javax.mail.MessagingException;
import java.io.IOException;

@PageTitle("Email")
@Route(value = "email", layout = MainLayout.class)
@PermitAll
public class EmailView extends VerticalLayout {

    private IMail emailService;
    private Email email;
    private Grid<Email> emailGrid = new Grid<>(Email.class);
    private Button newEmailButton = new Button("New Email");
    private TextField searchField = new TextField("Search emails");
    Button healthServiceButton = new Button("Check Mail Service", event -> {
        Notification.show("Our Mail Service is available to send fast and secure emails",
                3000, Notification.Position.MIDDLE);
    });

    public EmailView(IMail emailService) {
        this.emailService = emailService;
        setPadding(true);
        setMargin(true);
        emailGrid.setWidth("1200px");
        emailGrid.setHeight("400px");

        // Set up the email grid
        emailGrid.setColumns("to", "cc", "subject", "dateSent");


        EmailDialog emailDialog = new EmailDialog();

        // Set up the new email button
        newEmailButton.addClickListener(e -> {

            emailDialog.setSendButtonListener(event -> {
                String to = emailDialog.getToField();
                String cc = emailDialog.getCcField();
                String subject = emailDialog.getSubjectField();
                String message = emailDialog.getMessageEditor();
                try {
                    this.emailService.send(to, cc, subject, message);
                    Notification.show("Email sent");
                } catch (MessagingException ex) {
                    throw new RuntimeException(ex);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                email = emailService.saveEmail( to ,cc, subject, message);
                updateList ();
                emailDialog.close();
            });

            emailDialog.setCancelButtonListener(buttonClickEvent -> {
                emailDialog.close();
            });
            emailDialog.open();
        });

        // Set up the search field
        searchField.addValueChangeListener(event -> {
            String filter = event.getValue();
                emailGrid.setItems(emailService.findAll(filter));
        });

        HorizontalLayout toolbarLayout = new HorizontalLayout(searchField, newEmailButton, healthServiceButton);
        toolbarLayout.setVerticalComponentAlignment(Alignment.START, searchField);
        toolbarLayout.setVerticalComponentAlignment(Alignment.END,healthServiceButton);
        toolbarLayout.setVerticalComponentAlignment(Alignment.END, newEmailButton);

        // Add components to the layout
        add(toolbarLayout, emailGrid);
        updateList();
    }
    private void updateList() {
        emailGrid.setItems (emailService.findAll (searchField.getValue ()));
    }
}
