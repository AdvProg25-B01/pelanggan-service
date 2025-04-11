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
        // Untuk tahap RED, kita belum implementasi strategi sehingga lakukan fail() di test
        searchStrategy = null;
    }

    @Test
    public void testSearchByNameReturnsMatchingCustomers() {
        // Test seharusnya mencari keyword "Alice" dan mengembalikan 2 customer
        fail("Not yet implemented (RED).");
    }

    @Test
    public void testSearchByNameReturnsEmptyListForNoMatch() {
        // Test seharusnya mencari keyword yang tidak ada, misalnya "Charlie", mengembalikan list kosong
        fail("Not yet implemented (RED).");
    }
}
