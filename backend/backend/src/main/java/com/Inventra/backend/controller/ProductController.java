package com.Inventra.backend.controller;

import com.Inventra.backend.Entity.Product;
import com.Inventra.backend.Service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    private ProductService productService;
    @GetMapping
    public List<Product> getAllProducts(){
        return productService.getAllProducts();
    }
    @PostMapping
    public Product addProduct(@RequestBody Product product){
        return productService.addProduct(product);
    }
    @GetMapping("/low-stock")
    public List<Product> getLowStockAlerts(){
        return productService.getLowStockProducts();
    }
    @PutMapping("/{id}/sell")
    public com.Inventra.backend.Entity.Transaction sellProduct(@PathVariable Long id , @RequestParam Integer quantity){
        return productService.processSale(id , quantity);
    }
    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable Long id , @RequestBody Product product){
        return productService.updateProduct(id , product);
    }
    @DeleteMapping("/{id}")
    public void deleteProduct(@PathVariable Long id){
        productService.deleteProduct(id);
    }
    @GetMapping("/sku/{skuCode}")
    public Product getProductBySku(@PathVariable String skuCode){
        return productService.getProductbySku(skuCode);
    }

}
