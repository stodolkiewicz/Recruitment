package com.awin.recruitment.library;

import com.awin.recruitment.model.Transaction;
import com.awin.recruitment.model.TransactionWithTotalAmountPaid;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class TransactionProducer implements Producer<Transaction>, Runnable{
    BlockingQueue<Transaction> transactions = new LinkedBlockingQueue<>();
    BlockingQueue<TransactionWithTotalAmountPaid> transactionsWithTotalAmountPaid;

    public TransactionProducer(BlockingQueue<Transaction> transactions, BlockingQueue<TransactionWithTotalAmountPaid> transactionsWithTotalAmountPaid ) {
        this.transactions = transactions;
        this.transactionsWithTotalAmountPaid = transactionsWithTotalAmountPaid;
    }

    @Override
    public void run() {
        produce(transactions);
    }

    @Override
    public void produce(Iterable<Transaction> transactions) {
        List<TransactionWithTotalAmountPaid> processedTransactions = StreamSupport.stream(transactions.spliterator(), true)
                .map(transaction -> createTransactionWithTotalAmountPaid(transaction))
                .collect(Collectors.toList());

        System.out.println("New transactions processed!");
        System.out.println(processedTransactions);

        transactionsWithTotalAmountPaid.addAll(processedTransactions);
    }

    private TransactionWithTotalAmountPaid createTransactionWithTotalAmountPaid(Transaction transaction) {
        BigDecimal totalAmountPaid = transaction.getProducts()
                .stream()
                .map(product -> product.getAmountPaid())
                .reduce(BigDecimal.ZERO, (amountPaid1, amountPaid2) -> amountPaid1.add(amountPaid2));

        return new TransactionWithTotalAmountPaid(transaction, totalAmountPaid);
    }


    public BlockingQueue<TransactionWithTotalAmountPaid> getTransactionsWithTotalAmountPaid() {
        return transactionsWithTotalAmountPaid;
    }

    public void setTransactionsWithTotalAmountPaid(BlockingQueue<TransactionWithTotalAmountPaid> transactionsWithTotalAmountPaid) {
        this.transactionsWithTotalAmountPaid = transactionsWithTotalAmountPaid;
    }
}
