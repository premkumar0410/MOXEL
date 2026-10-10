package com.moxel.store.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "product_variant")
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false, length = 10)
    private String size;

    @Column(nullable = false, length = 40)
    private String color;

    @Column(nullable = false, unique = true, length = 60)
    private String sku;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false)
    private int stock;

    protected ProductVariant() { }

    public ProductVariant(String size, String color, String sku,
                          BigDecimal price, int stock) {
        this.size = size;
        this.color = color;
        this.sku = sku;
        this.price = price;
        this.stock = stock;
    }

    void setProduct(Product product) { this.product = product; }

    public Long getId() { return id; }
    public Product getProduct() { return product; }
    public String getSize() { return size; }
    public String getColor() { return color; }
    public String getSku() { return sku; }
    public BigDecimal getPrice() { return price; }
    public int getStock() { return stock; }
}