package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BlockchainVoterApplication {
    public static void main(String[] args) {

        System.out.println("DB_HOST = " + System.getenv("DB_HOST"));
        SpringApplication.run(BlockchainVoterApplication.class, args);
    }
}