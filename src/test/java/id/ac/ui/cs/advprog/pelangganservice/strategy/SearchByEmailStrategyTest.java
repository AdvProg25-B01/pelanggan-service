package id.ac.ui.cs.advprog.pelangganservice.strategy;

import id.ac.ui.cs.advprog.pelangganservice.model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class SearchByEmailStrategyTest {

    private SearchStrategy strategy;
    private List<Customer> customers;

    @BeforeEach
    void setUp() {
        strategy = new SearchByEmailStrategy();
        customers = Arrays.asList(
                Customer.builder().fullName("Alice").email("alice.wonder@example.com").build(),
                Customer.builder().fullName("Bob").email("bob.builder@example.com").build(),
                Customer.builder().fullName("Charlie").email("charlie@wonder.land").build()
        );
    }

    @Test
    void testSearch_exactMatch() {
        List<Customer> results = strategy.search(customers, "alice.wonder@example.com");
        assertEquals(1, results.size());
        assertEquals("alice.wonder@example.com", results.get(0).getEmail());
    }

    @Test
    void testSearch_partialMatch_domain() {
        List<Customer> results = strategy.search(customers, "example.com");
        assertEquals(2, results.size());
    }

    @Test
    void testSearch_partialMatch_user() {
        List<Customer> results = strategy.search(customers, "bob");
        assertEquals(1, results.size());
    }

    @Test
    void testSearch_caseInsensitive() {
        List<Customer> results = strategy.search(customers, "ALICE.WONDER@EXAMPLE.COM");
        assertEquals(1, results.size());
    }

    @Test
    void testSearch_noMatch() {
        List<Customer> results = strategy.search(customers, "zelda@hyrule.com");
        assertTrue(results.isEmpty());
    }

    @Test
    void testSearch_emptyCustomerList() {
        List<Customer> results = strategy.search(Collections.emptyList(), "test@example.com");
        assertTrue(results.isEmpty());
    }
}