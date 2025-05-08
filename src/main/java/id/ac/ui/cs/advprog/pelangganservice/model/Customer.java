package id.ac.ui.cs.advprog.pelangganservice.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;
// import java.util.List; // Untuk "transaksi pelanggan" jika akan di-detailkan nanti

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Customer {
    private UUID id;
    private String fullName;
    private String phoneNumber;
    private String email;
    private String address;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Placeholder untuk riwayat transaksi jika ingin ditampilkan
    // Nanti ini bisa diisi dari service lain atau query gabungan
    // private List<String> transactionHistorySummary;
}