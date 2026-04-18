package com.Inventra.backend.Util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class QrCodeGenerator {

    public byte[] generateLocationQr(String address, String mapLink) {
        String mapsUrl = "";

        try {
            // 1. If they provided a direct link, use that!
            if (mapLink != null && !mapLink.trim().isEmpty()) {
                mapsUrl = mapLink;
            }
            // 2. If they just typed an address, generate a Google Maps search for that text!
            else if (address != null && !address.trim().isEmpty()) {
                String encodedAddress = URLEncoder.encode(address, StandardCharsets.UTF_8.toString());
                mapsUrl = "https://www.google.com/maps/search/?api=1&query=" + encodedAddress;
            }
            // 3. Fallback just in case both are empty
            else {
                mapsUrl = "https://maps.google.com";
            }

            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(mapsUrl, BarcodeFormat.QR_CODE, 200, 200);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", out);

            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating QR Code", e);
        }
    }
}