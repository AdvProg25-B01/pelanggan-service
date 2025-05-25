package id.ac.ui.cs.advprog.pelangganservice.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    private Customer customer;
    private final UUID id = UUID.randomUUID();
    private final String fullName = "John Doe";
    private final String phoneNumber = "081234567890";
    private final String email = "john.doe@example.com";
    private final String address = "123 Main St";
    private final boolean isActive = true;
    private final LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
    private final LocalDateTime updatedAt = LocalDateTime.now();

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(id);
        customer.setFullName(fullName);
        customer.setPhoneNumber(phoneNumber);
        customer.setEmail(email);
        customer.setAddress(address);
        customer.setActive(isActive);
        customer.setCreatedAt(createdAt);
        customer.setUpdatedAt(updatedAt);
    }

    @Test
    void testCustomerGetters() {
        assertEquals(id, customer.getId());
        assertEquals(fullName, customer.getFullName());
        assertEquals(phoneNumber, customer.getPhoneNumber());
        assertEquals(email, customer.getEmail());
        assertEquals(address, customer.getAddress());
        assertEquals(isActive, customer.isActive());
        assertEquals(createdAt, customer.getCreatedAt());
        assertEquals(updatedAt, customer.getUpdatedAt());
    }

    @Test
    void testCustomerSetters() {
        UUID newId = UUID.randomUUID();
        String newFullName = "Jane Doe";
        // ... (set semua field baru)

        customer.setId(newId);
        customer.setFullName(newFullName);
        // ...

        assertEquals(newId, customer.getId());
        assertEquals(newFullName, customer.getFullName());
        // ...
    }

    @Test
    void testCustomerBuilder() {
        Customer builtCustomer = Customer.builder()
                .id(id)
                .fullName(fullName)
                .phoneNumber(phoneNumber)
                .email(email)
                .address(address)
                .isActive(isActive)
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        assertEquals(customer, builtCustomer); // Pastikan equals & hashCode diimplementasikan oleh @Data
    }

    @Test
    void testCustomerNoArgsConstructor() {
        Customer emptyCustomer = new Customer();
        assertNull(emptyCustomer.getId());
        assertNull(emptyCustomer.getFullName());
    }

    @Test
    void testCustomerAllArgsConstructor() {
        Customer allArgsCustomer = new Customer(id, fullName, phoneNumber, email, address, isActive, createdAt, updatedAt);
        assertEquals(id, allArgsCustomer.getId());
        assertEquals(fullName, allArgsCustomer.getFullName());
        assertEquals(phoneNumber, allArgsCustomer.getPhoneNumber());
        assertEquals(email, allArgsCustomer.getEmail());
        assertEquals(address, allArgsCustomer.getAddress());
        assertEquals(isActive, allArgsCustomer.isActive());
        assertEquals(createdAt, allArgsCustomer.getCreatedAt());
        assertEquals(updatedAt, allArgsCustomer.getUpdatedAt());
    }
}