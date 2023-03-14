package com.example.software.views.invoice.ExportUtils;

import com.example.software.data.entity.Employee;
import com.example.software.data.entity.InvoiceDetails;
import com.vaadin.flow.component.combobox.ComboBox;
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
import java.util.List;

public class PdfUtils {

    public byte[] createPdf(TextField invoiceName, TextField cFirstName, TextField cLastName,
                            DatePicker date, TextField cPhone, EmailField emailCustomer,
                            TextArea textArea, ComboBox<Employee> employee,List<InvoiceDetails> detailsList) throws IOException {

        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        // Create a new content stream for the page
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

        contentStream.beginText();
        contentStream.newLineAtOffset(50, 550);
        Employee selectedEmployee = employee.getValue();
        String employeeName = selectedEmployee.getFirstName();
        contentStream.showText("Employee: " + employeeName);
        contentStream.endText();

        // set table parameters  //table
        float margin = 65;
        float yStartNewPage = page.getMediaBox().getHeight() - (5 * margin);
        float tableWidth = page.getMediaBox().getWidth() - (1 * margin);
        float rowHeight = 20f;
        float tableTopY = yStartNewPage;
        float tableBottomY = margin;
        float[] columnWidths = {3f, 2f, 2f, 3f};

        float currentY = tableTopY;
        float currentX = margin;

        for (int i = 0; i < columnWidths.length; i++) {
            String header = "";
            switch (i) {
                case 0:
                    header = "Name";
                    break;
                case 1:
                    header = "Price";
                    break;
                case 2:
                    header = "Quantity";
                    break;
                case 3:
                    header = "Total";
                    break;
                case 4:
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

        for (InvoiceDetails product : detailsList) {
            currentY -= rowHeight;
            currentX = margin;
            contentStream.beginText();
            contentStream.newLineAtOffset(currentX, currentY);
          //  contentStream.showText(product.getProduct().getName());
            contentStream.endText();
            currentX += columnWidths[0] * (tableWidth / 14);


            contentStream.beginText();
            contentStream.newLineAtOffset(currentX, currentY);
           // contentStream.showText((product.getProduct().getPrice()).toString());
            contentStream.endText();
            currentX += columnWidths[1] * (tableWidth / 14);

            contentStream.beginText();
            contentStream.newLineAtOffset(currentX, currentY);
            contentStream.showText(String.valueOf(product.getQuantity()));
            contentStream.endText();
            currentX += columnWidths[2] * (tableWidth / 14);

            contentStream.beginText();
            contentStream.newLineAtOffset(currentX, currentY);
            contentStream.showText(String.valueOf(product.getTotal()));
            contentStream.endText();
            currentX += columnWidths[3] * (tableWidth / 14);
        }

        //footer
        currentY -= rowHeight;
        currentX = margin;

        contentStream.beginText();
        contentStream.newLineAtOffset(currentX, currentY);
        contentStream.showText("Total:");
        contentStream.endText();

        currentX += columnWidths[0] * (tableWidth / 10);

        contentStream.beginText();
        contentStream.newLineAtOffset(currentX, currentY);
        contentStream.showText("");
        contentStream.endText();

        currentX += columnWidths[1] * (tableWidth / 10);

//        contentStream.beginText();
//        contentStream.newLineAtOffset(currentX, currentY);
//        contentStream.showText(Integer.toString(totalQuantity));
//        contentStream.endText();
//
//        currentX += columnWidths[2] * (tableWidth / 10);
//
//        contentStream.beginText();
//        contentStream.newLineAtOffset(currentX, currentY);
//        contentStream.showText(Float.toString(totalValue));
//        contentStream.endText();


        // Close the content stream and save the PDF
        contentStream.close();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        document.save(outputStream);
        document.close();

        return outputStream.toByteArray();
    }
}