package id.ac.ui.cs.advprog.pelangganservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    @Column(unique = true) // Email bisa null jika tidak wajib, tapi jika ada harus unik
    private String email;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Builder.Default
    private boolean isActive = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    // Nantinya, jika ada relasi dengan Transaksi, bisa ditambahkan di sini.
    // Misalnya:
    // @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    // private List<Transaction> transactions;
    // Untuk saat ini, kita fokus pada data pelanggan saja.
}