package com.Inventra.backend.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Product{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String skuCode;
    private Double price;
    private Integer currentStock;
    private Integer reorderThreshold;
    public Product(){}

    public Product(Long id, String name, String skuCode, Double price, Integer currentStock, Integer reorderThreshold) {
        this.id = id;
        this.name = name;
        this.skuCode = skuCode;
        this.price = price;
        this.currentStock = currentStock;
        this.reorderThreshold = reorderThreshold;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSkuCode() {
        return skuCode;
    }

    public Double getPrice() {
        return price;
    }

    public Integer getCurrentStock() {
        return currentStock;
    }

    public Integer getReorderThreshold() {
        return reorderThreshold;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setCurrentStock(Integer currentStock) {
        this.currentStock = currentStock;
    }

    public void setReorderThreshold(Integer reorderThreshold) {
        this.reorderThreshold = reorderThreshold;
    }
}
