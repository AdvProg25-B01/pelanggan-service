package id.ac.ui.cs.advprog.pelangganservice.strategy;

import id.ac.ui.cs.advprog.pelangganservice.model.Customer;
import java.util.ArrayList;
import java.util.List;

public class NameSearchStrategy implements SearchStrategy {

    @Override
    public List<Customer> search(List<Customer> customers, String keyword) {
        List<Customer> results = new ArrayList<>();
        if (customers == null || keyword == null) return results;
        for (Customer customer : customers) {
            // Cari berdasarkan nama (case-insensitive)
            if (customer.getName().toLowerCase().contains(keyword.toLowerCase())) {
                results.add(customer);
            }
        }
        return results;
    }
}