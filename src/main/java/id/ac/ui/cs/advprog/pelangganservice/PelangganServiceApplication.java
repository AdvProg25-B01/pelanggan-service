package id.ac.ui.cs.advprog.pelangganservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class PelangganServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PelangganServiceApplication.class, args);
    }

}
