package id.ac.ui.cs.advprog.pelangganservice.repository;

import id.ac.ui.cs.advprog.pelangganservice.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository; // Tetap gunakan @Repository

import java.util.Optional;
import java.util.UUID;

@Repository // Anotasi ini penting agar Spring bisa mendeteksi dan membuat bean-nya
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    // Spring Data JPA akan secara otomatis mengimplementasikan:
    // - save(Customer customer) -> mengembalikan Customer yang disimpan (bisa create atau update)
    // - findById(UUID id) -> mengembalikan Optional<Customer>
    // - findAll() -> mengembalikan List<Customer>
    // - deleteById(UUID id) -> void
    // - count()
    // - existsById(UUID id)
    // dan banyak lagi.

    // Anda bisa menambahkan metode query kustom di sini, dan Spring Data JPA akan mencoba
    // membuatnya berdasarkan nama metode.
    Optional<Customer> findByEmail(String email); // Ini akan otomatis diimplementasikan

    // Jika Anda butuh query yang lebih kompleks, Anda bisa menggunakan @Query
    // Contoh (tidak diperlukan untuk kasus Anda saat ini):
    // @Query("SELECT c FROM Customer c WHERE c.fullName LIKE %:name%")
    // List<Customer> findByFullNameContains(@Param("name") String name);
}