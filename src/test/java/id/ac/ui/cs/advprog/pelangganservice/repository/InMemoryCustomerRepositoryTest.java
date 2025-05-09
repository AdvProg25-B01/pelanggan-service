package id.ac.ui.cs.advprog.pelangganservice.repository;

import id.ac.ui.cs.advprog.pelangganservice.model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryCustomerRepositoryTest {

    private InMemoryCustomerRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryCustomerRepository();
    }

    private Customer createSampleCustomer(String email) {
        return Customer.builder()
                .fullName("Test User")
                .phoneNumber("08123456789")
                .email(email)
                .address("123 Test St")
                .build();
    }

    @Test
    void testSaveNewCustomer() {
        Customer customer = createSampleCustomer("new@example.com");
        Customer savedCustomer = repository.save(customer);

        assertNotNull(savedCustomer.getId());
        assertNotNull(savedCustomer.getCreatedAt());
        assertNotNull(savedCustomer.getUpdatedAt());
        assertTrue(savedCustomer.isActive());
        assertEquals("Test User", savedCustomer.getFullName());
        assertEquals("new@example.com", savedCustomer.getEmail());

        Optional<Customer> foundCustomer = repository.findById(savedCustomer.getId());
        assertTrue(foundCustomer.isPresent());
        assertEquals(savedCustomer, foundCustomer.get());
    }

    @Test
    void testSaveUpdateCustomer() {
        Customer customer = createSampleCustomer("update@example.com");
        Customer savedCustomer = repository.save(customer); // Initial save
        UUID customerId = savedCustomer.getId();
        LocalDateTime initialUpdatedAt = savedCustomer.getUpdatedAt();

        // Simulate a delay for updatedAt check
        try { Thread.sleep(10); } catch (InterruptedException e) { /* ignore */ }

        savedCustomer.setFullName("Updated User");
        Customer updatedCustomer = repository.save(savedCustomer);

        assertEquals(customerId, updatedCustomer.getId());
        assertEquals("Updated User", updatedCustomer.getFullName());
        assertTrue(updatedCustomer.getUpdatedAt().isAfter(initialUpdatedAt));

        Optional<Customer> foundCustomer = repository.findById(customerId);
        assertTrue(foundCustomer.isPresent());
        assertEquals("Updated User", foundCustomer.get().getFullName());
    }

    @Test
    void testFindById_existing() {
        Customer customer = createSampleCustomer("findme@example.com");
        Customer savedCustomer = repository.save(customer);

        Optional<Customer> found = repository.findById(savedCustomer.getId());
        assertTrue(found.isPresent());
        assertEquals(savedCustomer.getEmail(), found.get().getEmail());
    }

    @Test
    void testFindById_nonExisting() {
        Optional<Customer> found = repository.findById(UUID.randomUUID());
        assertFalse(found.isPresent());
    }

    @Test
    void testFindAll_empty() {
        List<Customer> customers = repository.findAll();
        assertTrue(customers.isEmpty());
    }

    @Test
    void testFindAll_withData() {
        repository.save(createSampleCustomer("c1@example.com"));
        repository.save(createSampleCustomer("c2@example.com"));

        List<Customer> customers = repository.findAll();
        assertEquals(2, customers.size());
    }

    @Test
    void testDeleteById_existing() {
        Customer customer = createSampleCustomer("deleteme@example.com");
        Customer savedCustomer = repository.save(customer);
        UUID customerId = savedCustomer.getId();

        assertTrue(repository.findById(customerId).isPresent());
        repository.deleteById(customerId);
        assertFalse(repository.findById(customerId).isPresent());
    }

    @Test
    void testDeleteById_nonExisting() {
        // Should not throw error
        assertDoesNotThrow(() -> repository.deleteById(UUID.randomUUID()));
    }

    @Test
    void testFindByEmail_existing() {
        String email = "unique.email@example.com";
        repository.save(createSampleCustomer(email));
        repository.save(createSampleCustomer("other.email@example.com"));

        Optional<Customer> found = repository.findByEmail(email);
        assertTrue(found.isPresent());
        assertEquals(email, found.get().getEmail());
    }

    @Test
    void testFindByEmail_nonExisting() {
        repository.save(createSampleCustomer("another.email@example.com"));
        Optional<Customer> found = repository.findByEmail("nonexistent@example.com");
        assertFalse(found.isPresent());
    }

    @Test
    void testFindByEmail_caseInsensitive() {
        String email = "CaseTest@example.com";
        repository.save(createSampleCustomer(email));

        Optional<Customer> found = repository.findByEmail("casetest@example.com");
        assertTrue(found.isPresent());
        assertEquals(email, found.get().getEmail());
    }
}