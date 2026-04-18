package com.Inventra.backend.controller;

// ... (keep your existing imports) ...
import com.Inventra.backend.Entity.Product;
import com.Inventra.backend.Entity.Transaction;
import com.Inventra.backend.Util.ExcelGenerator;
import com.Inventra.backend.Util.PdfGenerator;
import com.Inventra.backend.repository.ProductRepository;
import com.Inventra.backend.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*")
public class TransactionController {
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private PdfGenerator pdfGenerator;
    @Autowired
    private ExcelGenerator excelGenerator;

    // ... (Keep downloadBill and downloadsalesExcel exactly the same) ...
    @GetMapping("/{id}/bill")
    public org.springframework.http.ResponseEntity<byte[]> downloadBill(@PathVariable Long id){
        Transaction transaction = transactionRepository.findById(id).orElseThrow(() -> new RuntimeException("Transaction not found"));
        Product product = productRepository.findById(transaction.getProductId()).orElse(new Product(0L, "Deleted Item", "N/A", 0.0, 0, 0));
        byte[] pdfBytes = pdfGenerator.generateBill(product, transaction);
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "bill" + id + ".pdf");
        return new org.springframework.http.ResponseEntity<>(pdfBytes, headers , org.springframework.http.HttpStatus.OK);
    }

    @GetMapping("/export")
    public org.springframework.http.ResponseEntity<byte[]> downloadsalesExcel(){
        java.util.List<com.Inventra.backend.Entity.Transaction> transactions = transactionRepository.findAll();
        byte[] excelBytes = excelGenerator.generateSalesExcel(transactions);
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", "sales_export.xlsx");
        return new org.springframework.http.ResponseEntity<>(excelBytes , headers , org.springframework.http.HttpStatus.OK);
    }

    // UPDATED CHALAN ENDPOINT
    @GetMapping("{id}/chalan")
    public org.springframework.http.ResponseEntity<byte[]> downloadChalan(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "") String address,
            @RequestParam(required = false, defaultValue = "") String mapLink,
            @RequestParam(required = false, defaultValue = "") String recipientName) { // NEW PARAMETER

        Transaction transaction = transactionRepository.findById(id).orElseThrow(() -> new RuntimeException("Transaction not found"));
        Product product = productRepository.findById(transaction.getProductId()).orElse(new Product(0L, "Deleted Item", "N/A", 0.0, 0, 0));

        // Pass the new recipientName to the generator
        byte[] pdfBytes = pdfGenerator.generateChalan(product, transaction, address, mapLink, recipientName);

        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "Chalan" + id + ".pdf");
        return new org.springframework.http.ResponseEntity<>(pdfBytes , headers , org.springframework.http.HttpStatus.OK);
    }
}