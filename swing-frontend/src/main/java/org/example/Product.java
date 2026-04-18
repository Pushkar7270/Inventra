package org.example;

public class Product {
    private Long id;
    private String name;
    private String skuCode;
    private Double price;
    private Integer currentStock;
    private Integer reorderThreshold;

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getSkuCode() { return skuCode; }
    public Double getPrice() { return price; }
    public Integer getCurrentStock() { return currentStock; }
    public Integer getReorderThreshold() { return reorderThreshold; }
}