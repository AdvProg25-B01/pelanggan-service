package id.ac.ui.cs.advprog.pelangganservice.command;

import id.ac.ui.cs.advprog.pelangganservice.model.Customer;
import id.ac.ui.cs.advprog.pelangganservice.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DeleteCustomerCommandTest {

    private FakeCustomerRepository repository;
    private DeleteCustomerCommand deleteCommand;

    @BeforeEach
    void setUp() {
        // Inisialisasi repository sederhana dan tambahkan 1 data customer
        repository = new FakeCustomerRepository();
        repository.save(new Customer("c001", "Alice", "alice@example.com"));

        deleteCommand = new DeleteCustomerCommand(repository);
    }

    @Test
    void testDeleteCustomerSuccess() {
        // Pastikan customer dengan id "c001" ada sebelum dihapus
        Customer customer = repository.findById("c001");
        assertNotNull(customer);

        // Eksekusi perintah hapus
        deleteCommand.execute("c001");

        // Setelah dihapus, pastikan customer tidak ditemukan
        Customer deleted = repository.findById("c001");
        assertNull(deleted);
    }

    @Test
    void testDeleteCustomerFailureWhenNotFound() {
        // Eksekusi perintah hapus untuk customer yang tidak ada
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            deleteCommand.execute("c999");
        });
        String expectedMessage = "Customer not found";
        assertTrue(ex.getMessage().contains(expectedMessage));
    }

    // FakeCustomerRepository sederhana untuk testing, ditambahkan metode delete
    private static class FakeCustomerRepository implements CustomerRepository {
        // Gunakan struktur penyimpanan sederhana, misalnya List
        private java.util.List<Customer> storage = new java.util.ArrayList<>();

        @Override
        public void save(Customer customer) {
            // Jika customer sudah ada, update
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
            Customer existing = findById(id);
            if (existing != null) {
                storage.remove(existing);
            } else {
                throw new IllegalArgumentException("Customer not found");
            }
        }
    }
}
