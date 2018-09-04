package com.awin.recruitment;

import com.awin.recruitment.infrastructure.spring.ClassPathXmlApplicationContextFactory;
import com.awin.recruitment.model.Product;
import com.awin.recruitment.model.Transaction;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class RecruitmentApp {

    private RecruitmentApp() { }

    public static void main(
        String[] args
    ) {

        ClassPathXmlApplicationContext applicationContext = ClassPathXmlApplicationContextFactory.create();

        System.out.println("Recruitment app is running");

        Transaction transaction = new Transaction.TransactionBuilder(465234L)
                .setSaleDate(LocalDate.of(2000, 1, 1))
                .addProduct(new Product("prodcutName1", BigDecimal.ONE))
                .addProduct(new Product("prodcutName2", BigDecimal.ONE))
                .build();

        System.out.println(transaction);
    }
}
