package br.com.medicadebolso;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication // Gerencia o scan de componentes por padrão
@EnableAsync
public class MedicaDeBolsoApplication {
    public static void main(String[] args) {
        SpringApplication.run(MedicaDeBolsoApplication.class, args);
    }
} 