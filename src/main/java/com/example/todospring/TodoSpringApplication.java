package com.example.todospring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class TodoSpringApplication {

    public static void main(String[] args) {

        SpringApplication.run(TodoSpringApplication.class, args);
    }

}
