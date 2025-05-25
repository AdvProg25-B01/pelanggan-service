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
    // @GeneratedValue(strategy = GenerationType.UUID) // Jika ingin database menghasilkan UUID
    // Jika Anda ingin tetap menggunakan UUID yang di-generate oleh aplikasi seperti sebelumnya,
    // maka tidak perlu @GeneratedValue. Pastikan ID di-set sebelum save jika null.
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

    // Jika Anda ingin mengatur nilai default untuk isActive, createdAt, updatedAt saat objek dibuat (sebelum persist)
    // Anda bisa menggunakan @PrePersist dan @PreUpdate, atau melakukannya di service layer
    // seperti yang sudah Anda lakukan. Dengan @CreationTimestamp dan @UpdateTimestamp,
    // Hibernate akan mengelolanya.

    // @PrePersist
    // protected void onCreate() {
    //     if (createdAt == null) {
    //         createdAt = LocalDateTime.now();
    //     }
    //     if (updatedAt == null) {
    //         updatedAt = LocalDateTime.now();
    //     }
    //     // isActive biasanya di-set secara eksplisit di service
    // }

    // @PreUpdate
    // protected void onUpdate() {
    //     updatedAt = LocalDateTime.now();
    // }
}