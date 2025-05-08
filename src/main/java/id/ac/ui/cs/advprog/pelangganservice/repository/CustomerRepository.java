package id.ac.ui.cs.advprog.pelangganservice.repository;

import id.ac.ui.cs.advprog.pelangganservice.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {
    // JpaRepository sudah menyediakan metode CRUD dasar seperti save, findById, findAll, deleteById, dll.
    // Anda bisa menambahkan metode kustom jika diperlukan, misalnya:
    // Optional<Customer> findByEmail(String email);
    // List<Customer> findAllByIsActiveTrue();
}