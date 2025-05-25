package id.ac.ui.cs.advprog.pelangganservice.strategy;

import id.ac.ui.cs.advprog.pelangganservice.model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class SearchByFullNameStrategyTest {

    private SearchStrategy strategy;
    private List<Customer> customers;

    @BeforeEach
    void setUp() {
        strategy = new SearchByFullNameStrategy();
        customers = Arrays.asList(
                Customer.builder().fullName("Alice Wonderland").email("alice@example.com").build(),
                Customer.builder().fullName("Bob The Builder").email("bob@example.com").build(),
                Customer.builder().fullName("Charlie Alice").email("charlie@example.com").build()
        );
    }

    @Test
    void testSearch_exactMatch() {
        List<Customer> results = strategy.search(customers, "Alice Wonderland");
        assertEquals(1, results.size());
        assertEquals("Alice Wonderland", results.get(0).getFullName());
    }

    @Test
    void testSearch_partialMatch() {
        List<Customer> results = strategy.search(customers, "Alice");
        assertEquals(2, results.size()); // Alice Wonderland, Charlie Alice
    }

    @Test
    void testSearch_caseInsensitive() {
        List<Customer> results = strategy.search(customers, "bob the builder");
        assertEquals(1, results.size());
        assertEquals("Bob The Builder", results.get(0).getFullName());
    }

    @Test
    void testSearch_noMatch() {
        List<Customer> results = strategy.search(customers, "Zelda");
        assertTrue(results.isEmpty());
    }

    @Test
    void testSearch_emptySearchTerm() {
        List<Customer> results = strategy.search(customers, "");
        assertEquals(customers.size(), results.size()); // Should return all or based on spec
    }

    @Test
    void testSearch_nullSearchTerm() {
        List<Customer> results = strategy.search(customers, null);
        assertEquals(customers.size(), results.size()); // Should return all or based on spec
    }

    @Test
    void testSearch_emptyCustomerList() {
        List<Customer> results = strategy.search(Collections.emptyList(), "Alice");
        assertTrue(results.isEmpty());
    }
}