package com.awin.transactionstoragetransactioneventextractor.domain.detector

import com.awin.recruitment.library.TransactionConsumer
import com.awin.recruitment.library.TransactionProducer
import com.awin.recruitment.model.Product
import com.awin.recruitment.model.Transaction
import com.awin.recruitment.model.TransactionWithTotalAmountPaid
import spock.lang.Specification

import java.time.LocalDate
import java.util.concurrent.BlockingQueue
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit

class RecruitmentAppTest extends Specification {

    int queueSize = 10;
    BigDecimal totalAmountPaidPoisonValue = BigDecimal.valueOf(-1L);
    int numberOfProducers = 2;
    int numberOfConsumers = 5;
    int poisonPillPerProducer = numberOfConsumers / numberOfProducers;
    int numberOfAdditionalPoisonPillsForOneOfTheProducers = numberOfConsumers % numberOfProducers;

    BlockingQueue<Transaction> transactions = new LinkedBlockingQueue<>(queueSize);
    BlockingQueue<TransactionWithTotalAmountPaid> transactionsWithTotalAmountPaid = new LinkedBlockingQueue<>(queueSize + numberOfConsumers);

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

    Transaction transaction5 = new Transaction.TransactionBuilder(5L)
            .setSaleDate(LocalDate.of(2100, 1, 1))
            .addProduct(new Product("prodcutName9", BigDecimal.valueOf(100L)))
            .addProduct(new Product("prodcutName10", BigDecimal.valueOf(100L)))
            .build();

    ExecutorService executorService;
    ExecutorService consumerExecutorService;

    def setup() {
        transactions.add(transaction1);
        transactions.add(transaction2);
        transactions.add(transaction3);
        transactions.add(transaction4);
        transactions.add(transaction5);
    }

    def "transactionsWithTotalAmountPaid should be empty after producers and consumers have done their job"() {
        setup:
            startProducersAndConsumers();
        when:
            waitForProducersAndCosumersTermination();
        then:
            transactionsWithTotalAmountPaid.size() == 0;
    }

    def "producers should produce transactionsWithTotalAmountPaid with the correct sumOfTotalAmountPaid "() {
        setup:
            BigDecimal expectedSumOfTotalAmountPaid = BigDecimal.valueOf(338L);
            startProducers();
        when:
            waitForProducersTermination();
            BigDecimal sumOfTotalAmountPaid = calculateSumOfTotalAmountPaid();
        then:
            transactionsWithTotalAmountPaid.size() == 10
            sumOfTotalAmountPaid.equals(expectedSumOfTotalAmountPaid)
    }

    def "producers should produce 5 poisonTransactions"() {
        setup:
            startProducers();
        when:
            waitForProducersTermination();
            int numberOfPoisonTransactions = calculateNumberOfPoisonTransactions();
        then:
            numberOfPoisonTransactions == numberOfConsumers;
    }

    def "producers shuld NOT throw nullPointerException when one of the transactions does not have products"() {
        setup:
            startProducers();
            Transaction transactionWithoutProducts = new Transaction.TransactionBuilder(404L)
                    .setSaleDate(LocalDate.of(2100, 1, 1))
                    .build();
            transactions.add(transactionWithoutProducts);
        when:
            waitForProducersTermination();
        then:
            1 == 1
    }

    //Utility methods ----------------------------------------------------------------------------------
    def startConsumers(){
        consumerExecutorService = Executors.newFixedThreadPool(numberOfProducers);
        for(int i = 0; i < numberOfConsumers; i++){
            consumerExecutorService.submit(new TransactionConsumer(transactionsWithTotalAmountPaid, totalAmountPaidPoisonValue));
        }
        consumerExecutorService.shutdown();
    }

    def startProducers(){
        executorService = Executors.newFixedThreadPool(numberOfProducers);
        executorService.submit(new TransactionProducer(transactions, transactionsWithTotalAmountPaid,
                totalAmountPaidPoisonValue, poisonPillPerProducer));
        executorService.submit(new TransactionProducer(transactions, transactionsWithTotalAmountPaid,
                totalAmountPaidPoisonValue, poisonPillPerProducer + numberOfAdditionalPoisonPillsForOneOfTheProducers));

        executorService.shutdown();
    }

    def startProducersAndConsumers(){
        startProducers();
        startConsumers();
    }

    def waitForProducersTermination(){
        while (!executorService.awaitTermination(24L, TimeUnit.HOURS)) {}
    }

    def waitForConsumersTermination(){
        while (!consumerExecutorService.awaitTermination(24L, TimeUnit.HOURS)) {}
    }

    def waitForProducersAndCosumersTermination(){
        waitForProducersTermination();
        waitForConsumersTermination();
    }

    def calculateSumOfTotalAmountPaid(){
        BigDecimal sumOfTotalAmountPaid = BigDecimal.ZERO;
        for(int i = 0; i < transactionsWithTotalAmountPaid.size(); i++){
            sumOfTotalAmountPaid = sumOfTotalAmountPaid.add( transactionsWithTotalAmountPaid.getAt(i).getTotalAmountPaid() );
        }
        return sumOfTotalAmountPaid;
    }

    def calculateNumberOfPoisonTransactions(){
        int numberOfPoisonTransactions = 0;
        for(int i = 0; i < transactionsWithTotalAmountPaid.size(); i++){
            if(transactionsWithTotalAmountPaid.getAt(i).totalAmountPaid.equals(BigDecimal.valueOf(-1L))){
                numberOfPoisonTransactions++;
            }
        }
        return numberOfPoisonTransactions;
    }
}
