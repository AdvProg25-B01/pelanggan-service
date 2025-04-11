package id.ac.ui.cs.advprog.pelangganservice.strategy;

import id.ac.ui.cs.advprog.pelangganservice.model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class NameSearchStrategyTest {

    private SearchStrategy searchStrategy;
    private List<Customer> customers;

    @BeforeEach
    public void setUp() {
        // Siapkan data testing
        customers = Arrays.asList(
                new Customer("c101", "Alice Johnson", "alice@example.com"),
                new Customer("c102", "Bob Smith", "bob@example.com"),
                new Customer("c103", "Alice Cooper", "alice.cooper@example.com")
        );
        // Inisialisasi strategi dengan implementasi NameSearchStrategy
        searchStrategy = new NameSearchStrategy();
    }

    @Test
    public void testSearchByNameReturnsMatchingCustomers() {
        // Mencari keyword "Alice" diharapkan menemukan 2 customer
        List<Customer> results = searchStrategy.search(customers, "Alice");
        assertEquals(2, results.size(), "Harusnya terdapat 2 customer yang cocok");
        for (Customer customer : results) {
            assertTrue(customer.getName().toLowerCase().contains("alice"), "Nama harus mengandung 'Alice'");
        }
    }

    @Test
    public void testSearchByNameReturnsEmptyListForNoMatch() {
        // Mencari keyword yang tidak ada, misalnya "Charlie", harus mengembalikan list kosong
        List<Customer> results = searchStrategy.search(customers, "Charlie");
        assertTrue(results.isEmpty(), "Hasil pencarian harus kosong jika tidak ada kecocokan");
    }
}