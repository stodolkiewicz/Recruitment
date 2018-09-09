package com.awin.recruitment.library;

import com.awin.recruitment.model.TransactionWithTotalAmountPaid;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;

public class TransactionConsumer implements Consumer<TransactionWithTotalAmountPaid>, Runnable{
    private BlockingQueue<TransactionWithTotalAmountPaid> transactionsWithTotalAmountPaid;
    private final BigDecimal totalAmountPaidPoisonValue;

    public TransactionConsumer(BlockingQueue<TransactionWithTotalAmountPaid> transactionsWithTotalAmountPaid, BigDecimal totalAmountPaidPoisonValue) {
        this.transactionsWithTotalAmountPaid = transactionsWithTotalAmountPaid;
        this.totalAmountPaidPoisonValue = totalAmountPaidPoisonValue;
    }

    @Override
    public void run() {
        while(true){
            try {
                TransactionWithTotalAmountPaid transaction = transactionsWithTotalAmountPaid.take();
                if(transaction.getTotalAmountPaid().equals(totalAmountPaidPoisonValue)){
                    return;
                }

                //consumer consumes 1 transaction at a time
                LinkedList<TransactionWithTotalAmountPaid>  transactionsToConsume = new LinkedList<>();
                transactionsToConsume.add(transaction);

                consume(transactionsToConsume);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void consume(Iterable<TransactionWithTotalAmountPaid> transactionsWithTotalAmountPaid) {
        System.out.println("om nom nom! consuming! Saving to db.");
    }

    public BlockingQueue<TransactionWithTotalAmountPaid> getTransactionsWithTotalAmountPaid() {
        return transactionsWithTotalAmountPaid;
    }

    public void setTransactionsWithTotalAmountPaid(BlockingQueue<TransactionWithTotalAmountPaid> transactionsWithTotalAmountPaid) {
        this.transactionsWithTotalAmountPaid = transactionsWithTotalAmountPaid;
    }

    public BigDecimal getTotalAmountPaidPoisonValue() {
        return totalAmountPaidPoisonValue;
    }
}
