package com.awin.recruitment;

import com.awin.recruitment.infrastructure.spring.ClassPathXmlApplicationContextFactory;
import com.awin.recruitment.library.TransactionProducer;
import com.awin.recruitment.model.Product;
import com.awin.recruitment.model.Transaction;
import com.awin.recruitment.model.TransactionWithTotalAmountPaid;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

public final class RecruitmentApp {

    private RecruitmentApp() { }

    public static void main(
        String[] args
    ) {

        ClassPathXmlApplicationContext applicationContext = ClassPathXmlApplicationContextFactory.create();

        System.out.println("Recruitment app is running");

        BlockingQueue<Transaction> transactions = new LinkedBlockingQueue<>();
        BlockingQueue<TransactionWithTotalAmountPaid> transactionsWithTotalAmountPaid = new LinkedBlockingQueue<>();

        Transaction transaction1 = new Transaction.TransactionBuilder(1L)
                .setSaleDate(LocalDate.of(2000, 1, 1))
                .addProduct(new Product("prodcutName1", BigDecimal.ONE))
                .addProduct(new Product("prodcutName2", BigDecimal.ONE))
                .build();

        Transaction transaction2 = new Transaction.TransactionBuilder(2L)
                .setSaleDate(LocalDate.of(2100, 1, 1))
                .addProduct(new Product("prodcutName3", BigDecimal.TEN))
                .addProduct(new Product("prodcutName4", BigDecimal.ONE))
                .build();

        transactions.add(transaction1);
        transactions.add(transaction2);

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.submit(new TransactionProducer(transactions, transactionsWithTotalAmountPaid));
        executorService.submit(new TransactionProducer(transactions, transactionsWithTotalAmountPaid));

        executorService.shutdown();

        System.out.println(transactions);


    }
}
