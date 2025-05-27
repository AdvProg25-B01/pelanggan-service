package id.ac.ui.cs.advprog.pelangganservice.model;

import jakarta.persistence.*; // Import dari jakarta.persistence
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.hibernate.annotations.CreationTimestamp; // Untuk createdAt otomatis
import org.hibernate.annotations.UpdateTimestamp;  // Untuk updatedAt otomatis

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity // Menandakan kelas ini sebagai entitas JPA
@Table(name = "customers") // Nama tabel di database (opsional, jika tidak sama dengan nama kelas case-insensitive)
public class Customer {

    @Id // Menandakan field ini sebagai primary key
    @Column(columnDefinition = "UUID", updatable = false, nullable = false) // Definisi kolom untuk UUID di PostgreSQL
    private UUID id;

    @Column(nullable = false) // Kolom tidak boleh null
    private String fullName;

    private String phoneNumber; // Bisa null

    @Column(nullable = false, unique = true) // Email harus unik dan tidak null
    private String email;

    private String address; // Bisa null

    @Column(nullable = false)
    private boolean isActive; // Menggunakan boolean primitif

    @CreationTimestamp // Otomatis diisi saat pembuatan
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp // Otomatis diisi saat pembuatan dan pembaruan
    @Column(nullable = false)
    private LocalDateTime updatedAt;

}