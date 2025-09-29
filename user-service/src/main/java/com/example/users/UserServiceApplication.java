package com.example.users;

import java.security.SecureRandom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


@SpringBootApplication
public class UserServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(UserServiceApplication.class, args);
  }
}

@RestController
class UserController {
    private final SecureRandom random = new SecureRandom();

    @GetMapping("/users_25KB")
    public String getUsers25KB() {
        // build a 25KB string
        return "User".repeat(5_000); // 5 chars × 50K ≈ 25KB
    }

    @GetMapping("/users_25MB")
    public String getUsers25MB() {
        // build a 25MB string
        return "User".repeat(5_000_000); // 5 chars × 5M ≈ 25MB
    }
}