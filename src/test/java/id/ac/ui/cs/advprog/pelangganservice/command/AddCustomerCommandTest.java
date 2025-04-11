package id.ac.ui.cs.advprog.pelangganservice.command;

import id.ac.ui.cs.advprog.pelangganservice.model.Customer;
import id.ac.ui.cs.advprog.pelangganservice.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AddCustomerCommandTest {

    // Kita pakai FakeRepository sebagai implementasi sederhana dari CustomerRepository
    private FakeCustomerRepository customerRepository;
    private AddCustomerCommand addCustomerCommand;

    @BeforeEach
    void setUp() {
        customerRepository = new FakeCustomerRepository();
        addCustomerCommand = new AddCustomerCommand(customerRepository);
    }

    @Test
    void testAddCustomerSuccess() {
        // 1. Buat Customer valid
        Customer customer = new Customer("c123", "Alice", "alice@example.com");

        // 2. Eksekusi command
        addCustomerCommand.execute(customer);

        // 3. Pastikan customer telah tersimpan di repository
        Customer savedCustomer = customerRepository.findById("c123");
        assertNotNull(savedCustomer);
        assertEquals("Alice", savedCustomer.getName());
    }

    @Test
    void testAddCustomerFailureWhenDataInvalid() {
        // 1. Buat Customer dengan nama kosong yang dianggap invalid
        Customer customer = new Customer("c124", "", "invalid@example.com");

        // 2. Eksekusi command dan pastikan dilempar exception
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            addCustomerCommand.execute(customer);
        });

        String expectedMessage = "Customer name cannot be empty";
        assertTrue(exception.getMessage().contains(expectedMessage));
    }

    // Fake Repository sederhana untuk testing
    private static class FakeCustomerRepository implements CustomerRepository {
        private List<Customer> storage = new ArrayList<>();

        @Override
        public void save(Customer customer) {
            storage.add(customer);
        }

        @Override
        public Customer findById(String id) {
            return storage.stream().filter(c -> c.getId().equals(id)).findFirst().orElse(null);
        }
    }
}