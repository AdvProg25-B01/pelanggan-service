package id.ac.ui.cs.advprog.pelangganservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

// Tambahkan anotasi validasi jika diperlukan
// import jakarta.validation.constraints.Email;
// import jakarta.validation.constraints.NotBlank;
// import jakarta.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateCustomerRequestDTO {
    // @NotBlank(message = "Full name is mandatory")
    // @Size(min = 3, max = 100, message = "Full name must be between 3 and 100 characters")
    private String fullName;

    private String phoneNumber; // Bisa opsional

    // @NotBlank(message = "Email is mandatory")
    // @Email(message = "Email should be valid")
    private String email;

    private String address; // Bisa opsional

    // `id`, `isActive`, `createdAt`, `updatedAt` tidak ada di sini karena akan di-handle oleh backend
}