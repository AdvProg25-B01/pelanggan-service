package id.ac.ui.cs.advprog.pelangganservice.strategy;

import id.ac.ui.cs.advprog.pelangganservice.model.Customer;
import java.util.List;
import java.util.stream.Collectors;

public class SearchByEmailStrategy implements SearchStrategy {
    @Override
    public List<Customer> search(List<Customer> customers, String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return customers;
        }
        String lowerCaseSearchTerm = searchTerm.toLowerCase();
        return customers.stream()
                .filter(customer -> customer.getEmail().toLowerCase().contains(lowerCaseSearchTerm))
                .collect(Collectors.toList());
    }
}