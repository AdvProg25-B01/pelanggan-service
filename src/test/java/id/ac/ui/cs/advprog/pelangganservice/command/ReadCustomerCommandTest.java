package id.ac.ui.cs.advprog.pelangganservice.command;

import id.ac.ui.cs.advprog.pelangganservice.model.Customer;
import id.ac.ui.cs.advprog.pelangganservice.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ReadCustomerCommandTest {

    private FakeCustomerRepository repository;
    private ReadCustomerCommand readCommand;

    @BeforeEach
    void setUp() {
        // Inisialisasi FakeCustomerRepository dan tambahkan satu customer
        repository = new FakeCustomerRepository();
        Customer customer = new Customer("c001", "Alice", "alice@example.com");
        repository.save(customer);
        // Inisialisasi command untuk membaca customer
        readCommand = new ReadCustomerCommand(repository);
    }

    @Test
    void testReadCustomerSuccess() {
        // Eksekusi read command dengan id yang ada
        Customer result = readCommand.execute("c001");
        assertNotNull(result);
        assertEquals("Alice", result.getName());
        assertEquals("alice@example.com", result.getEmail());
    }

    @Test
    void testReadCustomerFailureWhenNotFound() {
        // Eksekusi read command dengan id yang tidak ada
        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            readCommand.execute("c999");
        });
        String expectedMessage = "Customer not found";
        assertTrue(ex.getMessage().contains(expectedMessage));
    }

    // FakeCustomerRepository sederhana untuk keperluan testing
    private static class FakeCustomerRepository implements CustomerRepository {
        private java.util.List<Customer> storage = new java.util.ArrayList<>();

        @Override
        public void save(Customer customer) {
            // Jika customer sudah ada, lakukan update
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