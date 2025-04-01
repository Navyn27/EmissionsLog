package com.navyn.emissionlog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.jdbc.JdbcRepositoriesAutoConfiguration;

@SpringBootApplication
public class EmissionLogApplication {
    public static void main(String[] args) {
        SpringApplication.run(EmissionLogApplication.class, args);
    }
}