package br.com.medicadebolso;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@ComponentScan(excludeFilters = {
    @ComponentScan.Filter(type = FilterType.REGEX, pattern = "br\\.com\\.medicadebolso\\.controller\\..*"),
    @ComponentScan.Filter(type = FilterType.REGEX, pattern = "br\\.com\\.medicadebolso\\.api\\.controller\\..*")
})
public class MedicaDeBolsoApplication {
    public static void main(String[] args) {

        SpringApplication.run(MedicaDeBolsoApplication.class, args);
    }
} 