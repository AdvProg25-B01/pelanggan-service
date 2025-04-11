package id.ac.ui.cs.advprog.pelangganservice.command;

import id.ac.ui.cs.advprog.pelangganservice.model.Customer;
import id.ac.ui.cs.advprog.pelangganservice.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UpdateCustomerCommandTest {

    private FakeCustomerRepository repository;
    private UpdateCustomerCommand updateCommand;

    @BeforeEach
    void setUp() {
        // Inisialisasi repository sederhana dan tambah 1 dataa
        repository = new FakeCustomerRepository();
        repository.save(new Customer("c001", "Alice", "alice@example.com"));

        // Inisialisasi command dengan repository
        updateCommand = new UpdateCustomerCommand(repository);
    }

    @Test
    void testUpdateCustomerSuccess() {
        // 1. Siapkan Customer yang ingin di-update
        Customer updated = new Customer("c001", "Alice Updated", "alice_updated@example.com");

        // 2. Eksekusi command
        updateCommand.execute(updated);

        // 3. Ambil data dari repo dan pastikan sudah ter-update
        Customer result = repository.findById("c001");
        assertNotNull(result);
        assertEquals("Alice Updated", result.getName());
        assertEquals("alice_updated@example.com", result.getEmail());
    }

    @Test
    void testUpdateCustomerFailureWhenNotFound() {
        // 1. Buat objek dengan ID yang tidak ada di repo
        Customer notExist = new Customer("c999", "Ghost", "ghost@example.com");

        // 2. Eksekusi command dan harapkan exception
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            updateCommand.execute(notExist);
        });
        assertTrue(ex.getMessage().contains("Customer not found"),
                "Pesan error harus mengandung 'Customer not found'");
    }

    @Test
    void testUpdateCustomerFailureWhenNameEmpty() {
        // 1. Buat objek dengan ID valid, tapi nama kosong
        Customer invalid = new Customer("c001", "", "alice_new@example.com");

        // 2. Eksekusi command dan harapkan exception
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            updateCommand.execute(invalid);
        });
        assertTrue(ex.getMessage().contains("Customer name cannot be empty"),
                "Pesan error harus mengandung 'Customer name cannot be empty'");
    }

    // FakeCustomerRepository sederhana untuk testing
    private static class FakeCustomerRepository implements CustomerRepository {
        private List<Customer> storage = new ArrayList<>();

        @Override
        public void save(Customer customer) {
            // Jika customer sudah ada, update; jika tidak, tambahkan
            Customer existing = findById(customer.getId());
            if (existing == null) {
                storage.add(customer);
            } else {
                storage.remove(existing);
                storage.add(customer);
            }
        }

        @Override
        public Customer findById(String id) {
            return storage.stream()
                    .filter(c -> c.getId().equals(id))
                    .findFirst()
                    .orElse(null);
        }

        @Override
        public void delete(String id) {
            // Implementasi minimal untuk menghapus customer berdasarkan id:
            Customer existing = findById(id);
            if (existing != null) {
                storage.remove(existing);
            }
        }
    }
}
