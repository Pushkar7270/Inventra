package com.Inventra.backend.Util;


import com.Inventra.backend.Entity.Product;
import com.Inventra.backend.Entity.Transaction;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class PdfGenerator {
    @Autowired
    private QrCodeGenerator qrCodeGenerator;

    public byte[] generateBill(Product product , Transaction transaction){
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             PDDocument document = new PDDocument()) {

            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();

                // Set Header
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
                contentStream.newLineAtOffset(50, 700);
                contentStream.showText("Inventra Official Bill");

                // Set Body Text
                contentStream.setFont(PDType1Font.HELVETICA, 12);

                contentStream.newLineAtOffset(0, -40);
                contentStream.showText("Product Name: " + product.getName());

                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("SKU: " + product.getSkuCode());

                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Quantity Sold: " + transaction.getQuantity());

                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Total Price: $" + transaction.getTotalPrice());

                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Date: " + transaction.getTransactionDate().toString());

                contentStream.endText();
            }

            document.save(out);
            return out.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }
    public byte[] generateChalan(Product product, Transaction transaction, Double latitude, Double longitude , String deliveryAddress) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             PDDocument document = new PDDocument()) {

            PDPage page = new PDPage();
            document.addPage(page);

            // Generate the QR Code byte array
            byte[] qrBytes = qrCodeGenerator.generateLocationQr(latitude, longitude);
            org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject qrImage =
                    org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject.createFromByteArray(document, qrBytes, "QR");

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
                contentStream.newLineAtOffset(50, 700);
                contentStream.showText("Inventra Delivery Chalan");

                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.newLineAtOffset(0, -40);
                contentStream.showText("Deliver To Product: " + product.getName());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Quantity to Deliver: " + transaction.getQuantity());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Address: " + deliveryAddress);
                contentStream.endText();

                // Draw the QR Code image on the PDF (x=50, y=500, width=150, height=150)
                contentStream.drawImage(qrImage, 50, 480, 150, 150);
            }

            document.save(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generating Chalan PDF", e);
        }
    }
}
