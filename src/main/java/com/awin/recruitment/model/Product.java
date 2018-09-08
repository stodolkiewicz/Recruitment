package com.awin.recruitment.model;

import java.math.BigDecimal;

public class Product {

    private String productName;
    private BigDecimal amountPaid;

    public Product() {}

    public Product(String productName, BigDecimal amountPaid) {
        this.productName = productName;
        this.amountPaid = amountPaid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Product product = (Product) o;

        return getProductName().equals(product.getProductName());
    }

    @Override
    public int hashCode() {
        return getProductName().hashCode();
    }

    @Override
    public String toString() {
        return productName + " " + amountPaid;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(BigDecimal amountPaid) {
        this.amountPaid = amountPaid;
    }
}
