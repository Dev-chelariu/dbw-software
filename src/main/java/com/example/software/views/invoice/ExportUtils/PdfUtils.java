package com.example.software.views.invoice.ExportUtils;

import com.example.software.data.entity.Product;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PdfUtils {

    public byte[] generatePdf(TextField invoiceName,TextField cFirstName, TextField cLastName,
                              DatePicker date,TextField cPhone, EmailField emailCustomer,
                              TextArea textArea) throws IOException {

        // Generate the invoice PDF using PDFBox
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        // Set up a content stream for the page
        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        // Set the font
        PDFont font = PDType1Font.HELVETICA;
        contentStream.setFont(font, 12);

        // Add content to the PDF document
        contentStream.beginText();
        contentStream.newLineAtOffset(275, 750);
        contentStream.showText(invoiceName.getValue());
        contentStream.endText();

        // Add content to the PDF document
//                contentStream.beginText();
//                contentStream.newLineAtOffset(50, 720);
//                contentStream.showText("Nr. crt: " + invoice.getNrCrt());
//                contentStream.endText();

        contentStream.beginText();
        contentStream.newLineAtOffset(50, 700);
        contentStream.showText("Date: " + date.getValue());
        contentStream.endText();

        contentStream.beginText();
        contentStream.newLineAtOffset(50, 675);
        contentStream.showText("First Name: " + cFirstName.getValue());
        contentStream.endText();

        contentStream.beginText();
        contentStream.newLineAtOffset(50, 650);
        contentStream.showText("Last Name: " + cLastName.getValue());
        contentStream.endText();

        // Write the customer email
        contentStream.beginText();
        contentStream.newLineAtOffset(50, 625);
        contentStream.showText("Email: " + emailCustomer.getValue());
        contentStream.endText();

        // Write the customer phone
        contentStream.beginText();
        contentStream.newLineAtOffset(50, 600);
        contentStream.showText("Phone: " + cPhone.getValue());
        contentStream.endText();

        // Write the invoice details
        contentStream.beginText();
        contentStream.newLineAtOffset(50, 575);
        contentStream.showText("Details: " + textArea.getValue());
        contentStream.endText();

//        // Write the employee responsible
//        contentStream.beginText();
//        contentStream.newLineAtOffset(50, 550);
//        contentStream.showText("Employee: " + employee.getFirstName());
//        contentStream.endText();

        // set table parameters  //table
        float margin = 65;
        float yStartNewPage = page.getMediaBox().getHeight() - (5 * margin);
        float tableWidth = page.getMediaBox().getWidth() - (1 * margin);
        float rowHeight = 20f;
        float tableTopY = yStartNewPage;
        float tableBottomY = margin;
        float[] columnWidths = {1.5f, 3f, 2f, 2f, 1.5f, 2f, 2f, 2f};

        float currentY = tableTopY;
        float currentX = margin;

        for (int i = 0; i < columnWidths.length; i++) {
            String header = "";
            switch (i) {
                case 0:
                    header = "Product";
                    break;
                case 1:
                    header = "Description";
                    break;
                case 2:
                    header = "Price";
                    break;
                case 3:
                    header = "Currency";
                    break;
                case 4:
                    header = "Amount";
                    break;
                case 5:
                    header = "Status";
                    break;
                case 6:
                    header = "Total";
                    break;
                case 7:
                    header = "";
                    break;
            }
            contentStream.beginText();
            contentStream.newLineAtOffset(currentX, currentY);
            contentStream.showText(header);
            contentStream.endText();
            currentX += columnWidths[i] * (tableWidth / 14);
        }

        // draw table content
        List<Product> productList = new ArrayList<>();
        for (Product product : productList) {
            currentY -= rowHeight;
            currentX = margin;
            contentStream.beginText();
            contentStream.newLineAtOffset(currentX, currentY);
            //  contentStream.showText(product.getProducts().toString()); //todo: Check here
            contentStream.endText();
            currentX += columnWidths[0] * (tableWidth / 14);

            contentStream.beginText();
            contentStream.newLineAtOffset(currentX, currentY);
            //  contentStream.showText(Float.toString(invoice.getPrice()));
            contentStream.endText();
            currentX += columnWidths[2] * (tableWidth / 14);

//            contentStream.beginText();
//            contentStream.newLineAtOffset(currentX, currentY);
//            contentStream.showText(Integer.toString(product.getAmount()));
//            contentStream.endText();
//            currentX += columnWidths[4] * (tableWidth / 14);

//            contentStream.beginText();
//            contentStream.newLineAtOffset(currentX, currentY);
//            contentStream.showText(product.getOrderCompleted() ? "Completed" : "Open");
//            contentStream.endText();
//            currentX += columnWidths[5] * (tableWidth / 14);

//            contentStream.beginText();
//            contentStream.newLineAtOffset(currentX, currentY);
//            contentStream.showText(Float.toString(product.getTotal()));
//            contentStream.endText();
//            currentX += columnWidths[6] * (tableWidth / 14);

            // add a blank column at the end
            currentX += columnWidths[7] * (tableWidth / 14);
        }
        // draw table footer
        currentY -= rowHeight;
        currentX = margin;
        contentStream.beginText();
        contentStream.newLineAtOffset(currentX, currentY);
        contentStream.showText("Total:");
        contentStream.endText();

        currentX += columnWidths[0] * (tableWidth / 14);

        contentStream.beginText();
        contentStream.newLineAtOffset(currentX, currentY);
        contentStream.showText("");
        contentStream.endText();

        currentX += columnWidths[1] * (tableWidth / 14);

        contentStream.beginText();
        contentStream.newLineAtOffset(currentX, currentY);
        contentStream.showText("");
        contentStream.endText();

        currentX += columnWidths[2] * (tableWidth / 14);

        contentStream.beginText();
        contentStream.newLineAtOffset(currentX, currentY);
        contentStream.showText("");
        contentStream.endText();

        currentX += columnWidths[3] * (tableWidth / 14);

        int totalAmount = 0;
        float totalValue = 0.0f;


//                for (Invoice invoice : invoiceList) {
////                    totalAmount += invoice.getAmount();
////                    totalValue += invoice.getTotal();
////                }

        contentStream.beginText();
        contentStream.newLineAtOffset(currentX, currentY);
        contentStream.showText(Integer.toString(totalAmount));
        contentStream.endText();

        currentX += columnWidths[4] * (tableWidth / 14);

        contentStream.beginText();
        contentStream.newLineAtOffset(currentX, currentY);
        contentStream.showText("");
        contentStream.endText();

        currentX += columnWidths[5] * (tableWidth / 14);

        contentStream.beginText();
        contentStream.newLineAtOffset(currentX, currentY);
        contentStream.showText(Float.toString(totalValue));
        contentStream.endText();
        contentStream.close();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        document.save(out);
        document.close();

        return out.toByteArray();
    }
}