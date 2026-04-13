package com.Inventra.backend.Util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;

@Component
public class QrCodeGenerator {

    public byte[] generateLocationQr(Double latitude, Double longitude) {
        // Creates the Google Maps URL based on the coordinates
        String mapsUrl = "https://www.google.com/maps/search/?api=1&query=" + latitude + "," + longitude;

        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            // Generates a 200x200 pixel QR code matrix
            BitMatrix bitMatrix = qrCodeWriter.encode(mapsUrl, BarcodeFormat.QR_CODE, 200, 200);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            // Converts the matrix into a PNG image stream
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", out);

            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating QR Code", e);
        }
    }
}