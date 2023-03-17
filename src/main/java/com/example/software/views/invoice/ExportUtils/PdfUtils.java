package com.example.software.views.invoice.ExportUtils;

import com.example.software.data.entity.Employee;
import com.example.software.data.entity.Invoice;
import com.example.software.data.entity.InvoiceDetails;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PdfUtils {

    public byte[] createPdf(Invoice invoice) throws IOException {

        PDDocument document = new PDDocument();
        PDPage page = new PDPage(PDRectangle.A4);
        document.addPage(page);

        // Create a new content stream for the page
        PDPageContentStream contentStream = new PDPageContentStream(document, page);

        // Set the font
        PDFont font = PDType1Font.HELVETICA;
        int fontSize = 12;

        contentStream.setFont(font, fontSize);

        // Set the margins
        float margin = 50;
        float yStart = page.getMediaBox().getHeight() - margin;
        float yStartTable = yStart - (2 * fontSize);
        float tableWidth = page.getMediaBox().getWidth() - (2 * margin);

        // Write the heading
        String heading = "Invoice";
        float headingWidth = font.getStringWidth(heading) / 1000 * fontSize;
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, fontSize); // set font to bold
        contentStream.newLineAtOffset((page.getMediaBox().getWidth() - headingWidth) / 2, yStart);
        contentStream.showText(heading);
        contentStream.endText();

        // Write the customer information heading
        String customerHeading = "Customer Information";
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, fontSize); // set font to bold
        contentStream.newLineAtOffset(margin, yStart - (2 * fontSize));
        contentStream.showText(customerHeading);
        contentStream.endText();

        // Write the customer information
        float currentY = yStart - (3 * fontSize);
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, fontSize);
        contentStream.newLineAtOffset(margin, currentY - (2 * fontSize));
        contentStream.showText("First Name: " + invoice.getCustomer().getFirstName());
        contentStream.endText();

        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, fontSize);
        contentStream.newLineAtOffset(margin, currentY - (4 * fontSize));
        contentStream.showText("Last Name: " + invoice.getCustomer().getLastName());
        contentStream.endText();

        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, fontSize);
        contentStream.newLineAtOffset(margin, currentY - (6 * fontSize));
        contentStream.showText("Email: " + invoice.getCustomer().getEmail());
        contentStream.endText();

        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, fontSize);
        contentStream.newLineAtOffset(margin, currentY - (8 * fontSize));
        contentStream.showText("Phone: " + invoice.getCustomer().getPhone());
        contentStream.endText();

        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, fontSize);
        contentStream.newLineAtOffset(margin, currentY - (10 * fontSize));
        contentStream.showText("Customer details: " + invoice.getCustomer().getDetails());
        contentStream.endText();

        // employee info
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, fontSize);
        contentStream.newLineAtOffset(margin, currentY - (12 * fontSize));
        Employee selectedEmployee = invoice.getEmployee();
        String employeeName = selectedEmployee.getFirstName();
        contentStream.showText("Employee: " + employeeName);
        contentStream.endText();

        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, fontSize);
        contentStream.newLineAtOffset(margin, currentY - (14 * fontSize));
        contentStream.showText("Date: " + invoice.getInvoiceDate());
        contentStream.endText();

        float marginTable = 65;
        float yStartNewPage = page.getMediaBox().getHeight() - (5 * marginTable);
         tableWidth = page.getMediaBox().getWidth() - (2 * marginTable);
        float rowHeight = 20f;
        float tableTopY = yStartNewPage;
        float tableBottomY = marginTable;
        float[] columnWidths = {3f, 3f, 3f, 3f};

        float currentYTable = tableTopY;
        float currentXTable = marginTable;

        PDType1Font fontRegular = PDType1Font.HELVETICA;

        for (int i = 0; i < columnWidths.length; i++) {
            String header = "";
            switch (i) {
                case 0:
                    header = "Product";
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
                default:
                    break;
            }

            contentStream.setNonStrokingColor(Color.WHITE);
            contentStream.addRect(currentXTable, currentYTable, columnWidths[i] * (tableWidth / 14), rowHeight);
            contentStream.fill();
            contentStream.setNonStrokingColor(Color.BLACK);
            contentStream.beginText();
            contentStream.setFont(font, 12);
            contentStream.newLineAtOffset(currentXTable + 5, currentYTable + 5);
            contentStream.showText(header);
            contentStream.endText();
            currentXTable += columnWidths[i] * (tableWidth / 14);
        }

        // draw table content
        for (InvoiceDetails product : invoice.getInvoiceDetails()) {

            currentYTable -= rowHeight;
            currentXTable = margin;
            contentStream.setNonStrokingColor(Color.LIGHT_GRAY);
            contentStream.addRect(currentXTable, currentYTable, tableWidth, rowHeight);
            contentStream.fill();
            contentStream.setNonStrokingColor(Color.BLACK);

            contentStream.beginText();
            contentStream.setFont(fontRegular, 12);
            contentStream.newLineAtOffset(currentXTable + 25, currentYTable + 5);
            contentStream.showText(product.getProduct().getName());
            contentStream.endText();
            currentXTable +=1.5 * columnWidths[0] * (tableWidth / 14); // adjust the X position

            contentStream.beginText();
            contentStream.setFont(fontRegular, 12);
            contentStream.newLineAtOffset(currentXTable - 20, currentYTable + 5);
            contentStream.showText(product.getProduct().getPrice().toString());
            contentStream.endText();
            currentXTable +=1.2 * columnWidths[1] * (tableWidth / 14); // adjust the X position

            contentStream.beginText();
            contentStream.setFont(fontRegular, 12);
            contentStream.newLineAtOffset(currentXTable - 28, currentYTable + 5);
            contentStream.showText(String.valueOf(product.getQuantity()));
            contentStream.endText();
            currentXTable += 1.1 * columnWidths[2] * (tableWidth / 14); // adjust the X position

            contentStream.beginText();
            contentStream.setFont(fontRegular, 12);
            contentStream.newLineAtOffset(currentXTable - 50, currentYTable + 5);
            contentStream.showText(String.valueOf(product.getTotal()));
            contentStream.endText();
            currentXTable += 1 * columnWidths[3] * (tableWidth / 14); // adjust the X position
        }

        // draw footer
        currentYTable -= (2 * rowHeight); // add an extra row height for more space
        currentXTable = margin;
        contentStream.setNonStrokingColor(Color.WHITE);
        contentStream.addRect(currentXTable, currentYTable, tableWidth, rowHeight);
        contentStream.fill();
        contentStream.setNonStrokingColor(Color.BLACK);
        contentStream.beginText();
        contentStream.setFont(font, 12);
        contentStream.newLineAtOffset(currentXTable + 5, currentYTable + 5);
        contentStream.showText("Total: " + invoice.getTotal() + " lei");
        contentStream.endText();

        // Close the content stream and save the PDF
        contentStream.close();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        document.save(outputStream);
        document.close();

        return outputStream.toByteArray();
    }
}