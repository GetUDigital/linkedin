package com.boinew.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.boinew.api.repository.mysql")   // ← changed jpa to mysql
@EnableMongoRepositories(basePackages = "com.boinew.api.repository.mongo")

//@SpringBootApplication
//@EnableJpaRepositories(basePackages = "com.boinew.api.repository.jpa")
//@EnableMongoRepositories(basePackages = "com.boinew.api.repository.mongo")
public class BoiApplication {
    public static void main(String[] args) {
        SpringApplication.run(BoiApplication.class, args);
    }
}
