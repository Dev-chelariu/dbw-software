package com.example.software.views.email;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.richtexteditor.RichTextEditor;
import com.vaadin.flow.component.textfield.TextField;

public class EmailDialog extends Dialog {
    private H2 begin = new H2("New message");
    private TextField toField = new TextField("To:");
    private TextField ccField = new TextField("Cc:");
    private TextField subjectField = new TextField("Subject:");
    private RichTextEditor messageEditor = new RichTextEditor();
    private Button sendButton = new Button("Send");
    private Button cancelButton = new Button("Cancel");

     public EmailDialog() {
         // Set the dialog width and height
         setWidth("700px");
         setHeight("700px");
         setCloseOnEsc(true);
         setCloseOnOutsideClick(false);

        FormLayout formLayout = new FormLayout();

        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("250px", 2),
                new FormLayout.ResponsiveStep("250px", 3),
                new FormLayout.ResponsiveStep("250px", 3)
        );
        formLayout.add(begin, 1);
        formLayout.add(toField, 3);
        formLayout.add(ccField, 3);
        formLayout.add(subjectField, 4);
        formLayout.add(messageEditor, 5);

        HorizontalLayout buttonsLayout = new HorizontalLayout(sendButton, cancelButton);
        buttonsLayout.setSpacing(true);

        add(formLayout, buttonsLayout);
    }

    public void setSendButtonListener(ComponentEventListener<ClickEvent<Button>> listener) {
        sendButton.addClickListener(listener);
    }
    public void setCancelButtonListener(ComponentEventListener<ClickEvent<Button>> listener) {
        cancelButton.addClickListener(listener);
    }

    public void setToField(String to) {
        toField.setValue(to);
    }

    public void setCcField(String cc) {
        ccField.setValue(cc);
    }

    public void setSubjectField(String subject) {
        subjectField.setValue(subject);
    }

    public void setMessageEditor(String message) {
        messageEditor.setValue(message);
    }

    public String getToField() {
        return toField.getValue();
    }

    public String getCcField() {
        return ccField.getValue();
    }

    public String getSubjectField() {
        return subjectField.getValue();
    }

    public String getMessageEditor() {
        return messageEditor.getValue();
    }
}
