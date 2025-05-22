package id.ac.ui.cs.advprog.pelangganservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {

    @Bean(name = "taskExecutor") // Anda bisa memberi nama bean executor Anda
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);    // Jumlah thread inti
        executor.setMaxPoolSize(10);   // Jumlah maksimum thread
        executor.setQueueCapacity(25); // Kapasitas antrian sebelum thread baru dibuat (hingga maxPoolSize)
        executor.setThreadNamePrefix("AsyncCustomerTask-"); // Awalan nama thread
        executor.initialize();
        return executor;
    }
}