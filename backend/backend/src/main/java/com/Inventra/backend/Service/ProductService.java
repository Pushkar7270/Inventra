package com.Inventra.backend.Service;

import com.Inventra.backend.Entity.Product;
import com.Inventra.backend.Entity.Transaction;
import com.Inventra.backend.repository.ProductRepository;
import com.Inventra.backend.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    public List<Product> getAllProducts(){
        return productRepository.findAll();
    }

    public Product addProduct(Product product){
        if (product.getPrice() < 0 || product.getCurrentStock() < 0) {
            throw new IllegalArgumentException("Price and stock cannot be negative numbers.");
        }
        product.setReorderThreshold((int) (product.getCurrentStock() * 0.3));
        return productRepository.save(product);
    }

    public List<Product> getLowStockProducts(){
        List<Product> lowStockList = new ArrayList<>();
        for (Product product : getAllProducts()){
            if(product.getCurrentStock() <= product.getReorderThreshold()){
                lowStockList.add(product);
            }
        }
        return lowStockList;
    }

    public Transaction processSale(Long productId , Integer quantitySold){
        if (quantitySold == null || quantitySold <= 0) {
            throw new IllegalArgumentException("Sale quantity must be at least 1.");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found. Ensure the System Number is correct."));

        if (product.getCurrentStock() < quantitySold) {
            throw new IllegalStateException("Exceeded stock limit");
        }

        product.setCurrentStock(product.getCurrentStock() - quantitySold);
        Transaction transaction = new Transaction();
        transaction.setProductId(productId);
        transaction.setQuantity(quantitySold);
        transaction.setTotalPrice(product.getPrice() * quantitySold);
        productRepository.save(product);
        return transactionRepository.save(transaction);
    }

    public Product updateProduct(Long id , Product updatedData){
        if (updatedData.getPrice() < 0 || updatedData.getCurrentStock() < 0) {
            throw new IllegalArgumentException("Price and stock cannot be negative numbers.");
        }
        Product existing = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found in the database."));
        existing.setName(updatedData.getName());
        existing.setPrice(updatedData.getPrice());
        existing.setSkuCode(updatedData.getSkuCode());
        existing.setCurrentStock(updatedData.getCurrentStock());

        existing.setReorderThreshold((int) (updatedData.getCurrentStock() * 0.3));

        return productRepository.save(existing);
    }

    public void deleteProduct(Long id){
        productRepository.deleteById(id);
    }

    public Product getProductbySku(String skuCode){
        return productRepository.findBySkuCode(skuCode).orElseThrow(() -> new RuntimeException("Product not found"));
    }
}