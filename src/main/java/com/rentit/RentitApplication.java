package com.rentit;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class RentitApplication {

    public static void main(String[] args) {
        SpringApplication.run(RentitApplication.class, args);
    }

}
