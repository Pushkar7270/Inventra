package com.Inventra.backend.Util;

import org.springframework.stereotype.Component;

@Component
public class ExcelGenerator {
    public byte[] generateSalesExcel(java.util.List<com.Inventra.backend.Entity.Transaction> transactions) {
        try (org.apache.poi.ss.usermodel.Workbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
             java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream()) {

            org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("Sales Data");
            org.apache.poi.ss.usermodel.Row headerRow = sheet.createRow(0);
            String[] columns = {"Transaction ID", "Product ID", "Quantity", "Total Price", "Date"};

            for (int i = 0; i < columns.length; i++) {
                org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
            }

            int rowIdx = 1;
            for (com.Inventra.backend.Entity.Transaction t : transactions) {
                org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(t.getId() != null ? t.getId() : 0);
                row.createCell(1).setCellValue(t.getProductId() != null ? t.getProductId() : 0);
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
