package id.ac.ui.cs.advprog.pelangganservice.strategy;

import id.ac.ui.cs.advprog.pelangganservice.model.Customer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class SearchByPhoneNumberStrategyTest {

    private SearchStrategy strategy;
    private List<Customer> customers;

    @BeforeEach
    void setUp() {
        strategy = new SearchByPhoneNumberStrategy();
        customers = Arrays.asList(
                Customer.builder().fullName("Alice").phoneNumber("0812-3456-7890").build(),
                Customer.builder().fullName("Bob").phoneNumber("+62 856 1111 2222").build(),
                Customer.builder().fullName("Charlie").phoneNumber("081234560000").build()
        );
    }

    @Test
    void testSearch_exactMatch_normalized() {
        List<Customer> results = strategy.search(customers, "081234567890");
        assertEquals(1, results.size());
        assertEquals("0812-3456-7890", results.get(0).getPhoneNumber());
    }

    @Test
    void testSearch_partialMatch() {
        List<Customer> results = strategy.search(customers, "3456");
        assertEquals(2, results.size()); // Alice and Charlie
    }

    @Test
    void testSearch_withFormattingInSearchTerm() {
        List<Customer> results = strategy.search(customers, "0856-1111-2222");
        assertEquals(1, results.size());
        assertEquals("+62 856 1111 2222", results.get(0).getPhoneNumber());
    }

    @Test
    void testSearch_withCountryCodeInSearchTerm() {
        List<Customer> results = strategy.search(customers, "+6285611112222");
        assertEquals(1, results.size());
        assertEquals("+62 856 1111 2222", results.get(0).getPhoneNumber());
    }

    @Test
    void testSearch_noMatch() {
        List<Customer> results = strategy.search(customers, "9999");
        assertTrue(results.isEmpty());
    }

    @Test
    void testSearch_emptyCustomerList() {
        List<Customer> results = strategy.search(Collections.emptyList(), "0812");
        assertTrue(results.isEmpty());
    }
}