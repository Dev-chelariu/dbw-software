//package com.example.software.views.email;
//
//import com.example.software.data.entity.Email;
//import com.vaadin.flow.component.grid.Grid;
//import com.vaadin.flow.data.provider.ListDataProvider;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class EmailGrid extends Grid<Email> {
//
//    public EmailGrid(List<Email> messages) {
//        setItems(messages);
//        addColumn(Email::getTo).setHeader("To");
//        addColumn(Email::getSubject).setHeader("Subject");
//        addColumn(Email::getMessage).setHeader("Message");
//    }
//    public void addEmail(Email Email) {
//        ListDataProvider<Email> dataProvider = (ListDataProvider<Email>) getDataProvider();
//        List<Email> Emails = new ArrayList<>(dataProvider.getItems());
//        Emails.add(0, Email);
//        dataProvider = new ListDataProvider<>(Emails);
//        setDataProvider(dataProvider);
//        dataProvider.refreshAll();
//    }
//}
