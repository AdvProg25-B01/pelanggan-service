package id.ac.ui.cs.advprog.pelangganservice.strategy;

import id.ac.ui.cs.advprog.pelangganservice.model.Customer;
import java.util.List;
import java.util.stream.Collectors;

public class SearchByFullNameStrategy implements SearchStrategy {
    @Override
    public List<Customer> search(List<Customer> customers, String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return customers; // Atau return empty list jika tidak ada term dianggap tidak ada hasil
        }
        String lowerCaseSearchTerm = searchTerm.toLowerCase();
        return customers.stream()
                .filter(customer -> customer.getFullName().toLowerCase().contains(lowerCaseSearchTerm))
                .collect(Collectors.toList());
    }
}