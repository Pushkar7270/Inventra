package com.Inventra.backend.Util;

import com.Inventra.backend.Entity.Product;
import com.Inventra.backend.Entity.Transaction;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class PdfGenerator {
    @Autowired
    private QrCodeGenerator qrCodeGenerator;

    public byte[] generateBill(Product product , Transaction transaction){
        // Standard White-Background Bill remains unchanged
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             PDDocument document = new PDDocument()) {

            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
                contentStream.newLineAtOffset(50, 700);
                contentStream.showText("Inventra Official Bill");

                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.newLineAtOffset(0, -40);
                contentStream.showText("Product Name: " + product.getName());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Quantity Sold: " + transaction.getQuantity());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Total Price: Rs. " + transaction.getTotalPrice());
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

    // THE NEW "BLUEPRINT STYLE" SMART CHALAN
    public byte[] generateChalan(Product product, Transaction transaction, String deliveryAddress, String mapLink, String recipientName) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             PDDocument document = new PDDocument()) {

            // Use Standard A4 Size
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            byte[] qrBytes = qrCodeGenerator.generateLocationQr(deliveryAddress, mapLink);
            PDImageXObject qrImage = PDImageXObject.createFromByteArray(document, qrBytes, "QR");

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {

                // --- 1. DARK BLUEPRINT BACKGROUND ---
                Color bgColor = new Color(24, 34, 44);         // Dark slate blue
                Color accentColor = new Color(110, 180, 240);  // Tech Cyan/Light Blue
                Color whiteText = Color.WHITE;

                // Paint the whole page dark
                contentStream.setNonStrokingColor(bgColor);
                contentStream.addRect(0, 0, page.getMediaBox().getWidth(), page.getMediaBox().getHeight());
                contentStream.fill();

                // --- 2. HEADER: SMART CHALAN RECEIPT ---
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 22);
                contentStream.setNonStrokingColor(whiteText);
                contentStream.newLineAtOffset(160, 780);
                contentStream.showText("SMART CHALAN RECEIPT");
                contentStream.endText();

                // Decorative Underline
                contentStream.setStrokingColor(accentColor);
                contentStream.setLineWidth(1.5f);
                contentStream.moveTo(50, 765);
                contentStream.lineTo(545, 765);
                contentStream.stroke();

                // --- 3. RECIPIENT & DELIVERY BOX ---
                int startY = 730;
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.setNonStrokingColor(accentColor);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, startY);
                contentStream.showText("RECIPIENT & DELIVERY DETAILS");
                contentStream.endText();

                // Draw Outer Bounding Box
                contentStream.setLineWidth(1f);
                contentStream.addRect(50, startY - 80, 495, 70);
                contentStream.stroke();

                // Fill Box Data
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 11);
                contentStream.setNonStrokingColor(whiteText);
                contentStream.newLineAtOffset(60, startY - 25);
                String displayRecipient = (recipientName != null && !recipientName.isEmpty()) ? recipientName : "N/A";
                contentStream.showText("Recipient: " + displayRecipient);

                contentStream.newLineAtOffset(0, -20);
                String displayAddress = (deliveryAddress != null && !deliveryAddress.isEmpty()) ? deliveryAddress : "N/A";
                if(displayAddress.length() > 80) displayAddress = displayAddress.substring(0, 77) + "...";
                contentStream.showText("Address: " + displayAddress);

                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Receipt Ref: #" + transaction.getId() + "   |   Date: " + transaction.getTransactionDate().toLocalDate().toString());
                contentStream.endText();

                // --- 4. ITEMIZED LIST TABLE ---
                startY = 610;
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.setNonStrokingColor(accentColor);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, startY);
                contentStream.showText("ITEMIZED LIST");
                contentStream.endText();

                // Table Header Row Background
                contentStream.setNonStrokingColor(new Color(40, 60, 80));
                contentStream.addRect(50, startY - 30, 495, 20);
                contentStream.fill();

                // Table Header Row Border
                contentStream.setStrokingColor(accentColor);
                contentStream.addRect(50, startY - 30, 495, 20);
                contentStream.stroke();

                // Table Header Text
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 10);
                contentStream.setNonStrokingColor(whiteText);
                contentStream.newLineAtOffset(60, startY - 24);
                contentStream.showText("ITEM ID");
                contentStream.newLineAtOffset(80, 0);
                contentStream.showText("DESCRIPTION");
                contentStream.newLineAtOffset(200, 0);
                contentStream.showText("QTY");
                contentStream.newLineAtOffset(60, 0);
                contentStream.showText("UNIT PRICE");
                contentStream.endText();

                // Table Data Row Border
                contentStream.addRect(50, startY - 60, 495, 30);
                contentStream.stroke();

                // Table Data Text
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 10);
                contentStream.newLineAtOffset(60, startY - 50);
                contentStream.showText(product.getId() != null ? "ITEM " + product.getId() : "N/A");
                contentStream.newLineAtOffset(80, 0);
                contentStream.showText(product.getName());
                contentStream.newLineAtOffset(200, 0);
                contentStream.showText(String.valueOf(transaction.getQuantity()));
                contentStream.newLineAtOffset(60, 0);
                contentStream.showText("Rs. " + product.getPrice());
                contentStream.endText();

                // Totals Box (Right Aligned)
                contentStream.addRect(345, startY - 110, 200, 50);
                contentStream.stroke();

                contentStream.beginText();
                contentStream.newLineAtOffset(355, startY - 80);
                contentStream.showText("TAX (0%): Rs. 0.00");
                contentStream.newLineAtOffset(0, -20);
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.showText("TOTAL DUE: Rs. " + transaction.getTotalPrice());
                contentStream.endText();

                // --- 5. QR CODE & INSTRUCTIONS ---
                int qrY = startY - 320;

                // Instructions Label
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.setNonStrokingColor(accentColor);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, qrY + 160);
                contentStream.showText("DELIVERY INSTRUCTIONS");
                contentStream.endText();

                // Instructions Box
                contentStream.addRect(50, qrY, 300, 150);
                contentStream.stroke();

                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 11);
                contentStream.setNonStrokingColor(whiteText);
                contentStream.newLineAtOffset(60, qrY + 120);
                contentStream.showText("1. Scan the digital geolocation code.");
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("2. Navigate to coordinates via GPS.");
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("3. Verify package contents with recipient.");
                contentStream.newLineAtOffset(0, -40);
                contentStream.setFont(PDType1Font.HELVETICA_OBLIQUE, 10);
                contentStream.setNonStrokingColor(accentColor);
                contentStream.showText("Generated automatically by Inventra POS.");
                contentStream.endText();

                // QR Area Label
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 11);
                contentStream.setNonStrokingColor(accentColor);
                contentStream.beginText();
                contentStream.newLineAtOffset(370, qrY + 160);
                contentStream.showText("DIGITAL GEOLOCATION CODE");
                contentStream.endText();

                // QR Outer Tech Box
                contentStream.addRect(370, qrY, 175, 150);
                contentStream.stroke();

                // Draw the actual QR Code Image slightly inset inside the box
                contentStream.drawImage(qrImage, 387, qrY + 5, 140, 140);
            }

            document.save(out);
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error generating Blueprint Chalan PDF", e);
        }
    }
}