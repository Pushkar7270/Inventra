package com.Inventra.backend.Service;

import com.Inventra.backend.Entity.Product;
import com.Inventra.backend.Entity.Transaction;
import com.Inventra.backend.repository.ProductRepository;
import com.Inventra.backend.repository.TransactionRepository;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    public List<Product> getAllProducts(){
        return productRepository.findAll();
    }
    public Product addProduct(Product product){
        return productRepository.save(product);
    }
    public List<Product> getLowStockProducts(){
        List<Product> lowStockList = new ArrayList<>();
        for (Product product : getAllProducts()){
            if(product.getCurrentStock() < product.getReorderThreshold()){
                lowStockList.add(product);
            }
        }
        return lowStockList;
    }

    public Transaction processSale(Long productId , Integer quantitySold){
        Product product = productRepository.findById(productId).orElseThrow(() -> new RuntimeException("Product not found"));
        product.setCurrentStock(product.getCurrentStock() - quantitySold);
        Transaction transaction = new Transaction();
        transaction.setProductId(productId);
        transaction.setQuantity(quantitySold);
        transaction.setTotalPrice(product.getPrice() * quantitySold);
        productRepository.save(product);
        return transactionRepository.save(transaction); // Return the transaction!
    }

    public Product updateProduct(Long id , Product updatedData){
        Product existing = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        existing.setName(updatedData.getName());
        existing.setPrice(updatedData.getPrice());
        existing.setSkuCode(updatedData.getSkuCode());
        existing.setReorderThreshold(updatedData.getReorderThreshold());
        existing.setCurrentStock(updatedData.getCurrentStock());
        return productRepository.save(existing);

    }

    public void deleteProduct(Long id){
        productRepository.deleteById(id);
    }

    public Product getProductbySku(String skuCode){
        return productRepository.findBySkuCode(skuCode).orElseThrow(() -> new RuntimeException("Product not found"));
    }
    @Autowired
    private TransactionRepository transactionRepository;

}
