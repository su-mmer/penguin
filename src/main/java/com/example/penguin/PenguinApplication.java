package com.example.penguin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@RestController
@SpringBootApplication
public class PenguinApplication {

    public static void main(String[] args) {
        SpringApplication.run(PenguinApplication.class, args);
    }

    @GetMapping(value = "/")
    public String doGetHelloWorld() {
        return "Hello World";
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
}
