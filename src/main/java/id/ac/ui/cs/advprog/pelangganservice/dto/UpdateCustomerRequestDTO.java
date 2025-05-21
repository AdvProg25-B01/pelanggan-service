package id.ac.ui.cs.advprog.pelangganservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

// import jakarta.validation.constraints.Email;
// import jakarta.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCustomerRequestDTO {
    // @Size(min = 3, max = 100, message = "Full name must be between 3 and 100 characters")
    private String fullName; // Opsional, hanya update jika ada

    private String phoneNumber; // Opsional

    // @Email(message = "Email should be valid")
    private String email; // Opsional

    private String address; // Opsional

    // Pertimbangkan apakah isActive bisa diubah melalui update biasa.
    // Jika ya, tambahkan:
    // private Boolean isActive; // Gunakan Boolean agar bisa null (tidak ada perubahan)
    // Jika tidak, jangan sertakan.
}