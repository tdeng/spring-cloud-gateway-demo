package com.example.bytes;

import java.security.SecureRandom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@SpringBootApplication
public class ByteServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(ByteServiceApplication.class, args);
  }
}

@RestController
class ByteController {
    private final SecureRandom random = new SecureRandom();

    @GetMapping("/bytes_25KB")
    @ResponseBody
    public byte[] getBytes25KB() {
        byte[] data = new byte[25_000];     // ~25 KB
        random.nextBytes(data);             // fill with random bytes
        return data;
    }

    @GetMapping("/bytes_25MB")
    @ResponseBody
    public byte[] getBytes25MB() {
        byte[] data = new byte[25_000_000]; // ~25 MB
        random.nextBytes(data);             // fill with random bytes
        return data;
    }
}