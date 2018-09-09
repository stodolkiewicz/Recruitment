package com.awin.recruitment.library;

import com.awin.recruitment.model.Product;
import com.awin.recruitment.model.Transaction;
import com.awin.recruitment.model.TransactionWithTotalAmountPaid;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class TransactionProducer implements Producer<Transaction>, Runnable{
    BlockingQueue<Transaction> transactions = new LinkedBlockingQueue<>();
    BlockingQueue<TransactionWithTotalAmountPaid> transactionsWithTotalAmountPaid;

    private static final int MAX_NUMBER_OF_TRANSACTIONS_TO_PROCESS_IN_BATCH = 2;
    List<Transaction> transactionsBatch = new LinkedList<>();

    private final BigDecimal totalAmountPaidPoisonValue;
    private final int poisonPillsNumber;

    public TransactionProducer(BlockingQueue<Transaction> transactions, BlockingQueue<TransactionWithTotalAmountPaid> transactionsWithTotalAmountPaid, BigDecimal totalAmountPaidPoisonValue, int poisonPillsNumber) {
        this.transactions = transactions;
        this.transactionsWithTotalAmountPaid = transactionsWithTotalAmountPaid;
        this.totalAmountPaidPoisonValue = totalAmountPaidPoisonValue;
        this.poisonPillsNumber = poisonPillsNumber;
    }

    @Override
    public void run() {
        while(transactions.size() != 0){
            for(int i = 0; i < MAX_NUMBER_OF_TRANSACTIONS_TO_PROCESS_IN_BATCH; i++){
                Transaction polledTransaction = transactions.poll();
                if(polledTransaction != null){
                    transactionsBatch.add(polledTransaction);
                }
            }
            produce(transactionsBatch);
            transactionsBatch.clear();
        }

        //add poison Pill transaction
        try {
            for(int i = 0; i < poisonPillsNumber; i++){

                Transaction poisonPillTransaction = new Transaction();
                poisonPillTransaction.setId(-1L);

                TransactionWithTotalAmountPaid poisonPill = new TransactionWithTotalAmountPaid(poisonPillTransaction, totalAmountPaidPoisonValue);
                transactionsWithTotalAmountPaid.put(poisonPill);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void produce(Iterable<Transaction> transactions) {

        List<TransactionWithTotalAmountPaid> processedTransactions = StreamSupport.stream(transactions.spliterator(), true)
                .map(this::createTransactionWithTotalAmountPaid)
                .collect(Collectors.toList());

        System.out.println("New transactions processed! " + getCurrentThreadName() + "\ntransaction ids: " + getTransactionsBatchIds() + "\n");
        System.out.println("processed Transactions: " + processedTransactions);

        transactionsWithTotalAmountPaid.addAll(processedTransactions);
    }

    private TransactionWithTotalAmountPaid createTransactionWithTotalAmountPaid(Transaction transaction) {
        BigDecimal totalAmountPaid = transaction.getProducts()
                .stream()
                .map(Product::getAmountPaid)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new TransactionWithTotalAmountPaid(transaction, totalAmountPaid);
    }

    //debugging helper methods -------------------------------------------------
    private String getCurrentThreadName(){
        return Thread.currentThread().getName();
    }

    private String getTransactionsBatchIds(){
        StringBuilder ids = new StringBuilder();
        for(int i = 0; i < transactionsBatch.size(); i++){
            ids.append(transactionsBatch.get(i).getId()).append(" ");
        }
        return ids.toString();
    }

    public BlockingQueue<TransactionWithTotalAmountPaid> getTransactionsWithTotalAmountPaid() {
        return transactionsWithTotalAmountPaid;
    }

    public void setTransactionsWithTotalAmountPaid(BlockingQueue<TransactionWithTotalAmountPaid> transactionsWithTotalAmountPaid) {
        this.transactionsWithTotalAmountPaid = transactionsWithTotalAmountPaid;
    }
}
