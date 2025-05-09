package id.ac.ui.cs.advprog.pelangganservice.strategy;

import id.ac.ui.cs.advprog.pelangganservice.model.Customer;
import java.util.List;
import java.util.stream.Collectors;

public class SearchByPhoneNumberStrategy implements SearchStrategy {
    @Override
    public List<Customer> search(List<Customer> customers, String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return customers;
        }
        // Nomor teleponm biasanya tidak case-sensitive, tapi bisa mengandung spasi/simbol
        String normalizedSearchTerm = searchTerm.replaceAll("[^0-9]", "");
        return customers.stream()
                .filter(customer -> customer.getPhoneNumber().replaceAll("[^0-9]", "").contains(normalizedSearchTerm))
                .collect(Collectors.toList());
    }
}