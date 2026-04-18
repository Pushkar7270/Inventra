package com.Inventra.backend.Util;

import com.Inventra.backend.Entity.Product;
import com.Inventra.backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ExcelGenerator {

    // We add this so the Excel generator can search for the Product Names!
    @Autowired
    private ProductRepository productRepository;

    public byte[] generateSalesExcel(java.util.List<com.Inventra.backend.Entity.Transaction> transactions) {
        try (org.apache.poi.ss.usermodel.Workbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
             java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream()) {

            org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("Sales Data");
            org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(0);

            // Clean, business-friendly columns
            String[] columns = {"Product Name", "Price per Item", "Quantity Sold", "Total Price", "Date"};

            for (int i = 0; i < columns.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
            }

            int rowIdx = 1;
            for (com.Inventra.backend.Entity.Transaction t : transactions) {
                org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowIdx++);

                // Fetch the actual product details from the database using the Product ID
                Product product = productRepository.findById(t.getProductId()).orElse(null);

                String productName = (product != null) ? product.getName() : "Unknown (Deleted Item)";
                Double price = (product != null && product.getPrice() != null) ? product.getPrice() : 0.0;

                row.createCell(0).setCellValue(productName);
                row.createCell(1).setCellValue(price);
                row.createCell(2).setCellValue(t.getQuantity() != null ? t.getQuantity() : 0);
                row.createCell(3).setCellValue(t.getTotalPrice() != null ? t.getTotalPrice() : 0.0);
                row.createCell(4).setCellValue(t.getTransactionDate() != null ? t.getTransactionDate().toString() : "");
            }

            workbook.write(out);
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating Excel file", e);
        }
    }
}