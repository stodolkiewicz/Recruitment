package com.awin.recruitment;

import com.awin.recruitment.infrastructure.spring.ClassPathXmlApplicationContextFactory;
import com.awin.recruitment.library.TransactionProducer;
import com.awin.recruitment.model.Product;
import com.awin.recruitment.model.Transaction;
import com.awin.recruitment.model.TransactionWithTotalAmountPaid;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.concurrent.*;

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

        Transaction transaction3 = new Transaction.TransactionBuilder(3L)
                .setSaleDate(LocalDate.of(2100, 1, 1))
                .addProduct(new Product("prodcutName5", BigDecimal.TEN))
                .addProduct(new Product("prodcutName6", BigDecimal.TEN))
                .build();

        Transaction transaction4 = new Transaction.TransactionBuilder(4L)
                .setSaleDate(LocalDate.of(2100, 1, 1))
                .addProduct(new Product("prodcutName7", BigDecimal.TEN))
                .addProduct(new Product("prodcutName8", BigDecimal.valueOf(100L)))
                .build();

        transactions.add(transaction1);
        transactions.add(transaction2);
        transactions.add(transaction3);
        transactions.add(transaction4);

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        executorService.submit(new TransactionProducer(transactions, transactionsWithTotalAmountPaid));
        executorService.submit(new TransactionProducer(transactions, transactionsWithTotalAmountPaid));

        executorService.shutdown();
        try {
            while (!executorService.awaitTermination(24L, TimeUnit.HOURS)) {
                System.out.println("Not yet. Still waiting for termination");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("\nResults after program finished: " + transactionsWithTotalAmountPaid);


    }
}
