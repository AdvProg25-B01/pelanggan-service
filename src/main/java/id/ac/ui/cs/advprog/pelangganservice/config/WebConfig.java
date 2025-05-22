package id.ac.ui.cs.advprog.pelangganservice.config; // Sesuaikan package

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // Path yang ingin diizinkan CORS
                .allowedOrigins("http://localhost:3000") // URL frontend Anda
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Metode HTTP yang diizinkan
                .allowedHeaders("*") // Header yang diizinkan
                .allowCredentials(true); // Jika Anda menggunakan cookies/session
    }
}