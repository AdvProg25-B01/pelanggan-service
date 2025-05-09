package id.ac.ui.cs.advprog.pelangganservice.strategy;

import id.ac.ui.cs.advprog.pelangganservice.model.Customer;
import java.util.List;

public interface SearchStrategy {
    List<Customer> search(List<Customer> customers, String searchTerm);
}