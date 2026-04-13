package com.Inventra.backend.controller;

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
    @GetMapping("/{id}/bill")
    public org.springframework.http.ResponseEntity<byte[]> downloadBill(@PathVariable Long id){
        Transaction transaction = transactionRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        byte[] pdfBytes = pdfGenerator.generateBill(product, transaction);
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "bill" + id + ".pdf");
        return new org.springframework.http.ResponseEntity<>(pdfBytes, headers , org.springframework.http.HttpStatus.OK);

    }
    @GetMapping("{id}/chalan")
    public org.springframework.http.ResponseEntity<byte[]> downloadChalan(@PathVariable Long id , @RequestParam Double lat , @RequestParam Double lng , @RequestParam String address){
        Transaction transaction = transactionRepository.findById(id).orElseThrow(() -> new RuntimeException("Transaction not found"));
        Product product = productRepository.findById(transaction.getProductId()).orElseThrow(() -> new RuntimeException("Product not found"));
        byte[] pdfBytes = pdfGenerator.generateChalan(product, transaction , lat , lng , address);
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "Chalan" + id + ".pdf");
        return new org.springframework.http.ResponseEntity<>(pdfBytes , headers , org.springframework.http.HttpStatus.OK);

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
}
