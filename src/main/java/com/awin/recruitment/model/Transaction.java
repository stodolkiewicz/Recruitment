package com.awin.recruitment.model;

import com.sun.xml.internal.bind.v2.TODO;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

public class Transaction {
    private Long id;
    private LocalDate saleDate;
    private List<Product> products = new LinkedList<>();

    public Transaction() {}

    public Transaction(Long id, LocalDate saleDate, List<Product> products) {
        this.id = id;
        this.saleDate = saleDate;
        this.products = products;
    }

    public static class TransactionBuilder{
        private Long id;
        private LocalDate saleDate;
        private List<Product> products = new LinkedList<>();

        public TransactionBuilder(Long id){
            this.id = id;
        }

        public TransactionBuilder setSaleDate(LocalDate saleDate){
            this.saleDate = saleDate;
            return this;
        }

        public TransactionBuilder setProducts(List<Product> products){
            this.products = products;
            return this;
        }

        public TransactionBuilder addProduct(Product product){
            this.products.add(product);
            return this;
        }

        public Transaction build(){
            return new Transaction(id, saleDate, products);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Transaction that = (Transaction) o;

        return getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", products=" + products +
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(LocalDate saleDate) {
        this.saleDate = saleDate;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public void addProduct(Product product){
        this.products.add(product);
    }
}
