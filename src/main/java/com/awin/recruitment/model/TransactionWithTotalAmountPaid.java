package com.awin.recruitment.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

//prototype?
public class TransactionWithTotalAmountPaid extends Transaction{

    private Transaction transaction;
    private BigDecimal totalAmountPaid;

    public TransactionWithTotalAmountPaid() {}

    public TransactionWithTotalAmountPaid(Transaction transaction, BigDecimal totalAmountPaid) {
        this.transaction = transaction;
        this.totalAmountPaid = totalAmountPaid;
    }

    @Override
    public String toString() {
        return
                " tid: " + transaction.getId() +
                " totalAmountPaid: " + totalAmountPaid + "\n";
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public BigDecimal getTotalAmountPaid() {
        return totalAmountPaid;
    }

    public void setTotalAmountPaid(BigDecimal totalAmountPaid) {
        this.totalAmountPaid = totalAmountPaid;
    }
}
