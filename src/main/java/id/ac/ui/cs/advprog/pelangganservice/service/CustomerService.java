package id.ac.ui.cs.advprog.pelangganservice.service;

import id.ac.ui.cs.advprog.pelangganservice.model.Customer;
import id.ac.ui.cs.advprog.pelangganservice.strategy.SearchStrategy;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerService {
    Customer createCustomer(Customer customer);
    List<Customer> getAllCustomers();
    Optional<Customer> getCustomerById(UUID id);
    Optional<Customer> updateCustomer(UUID id, Customer customerDetails);
    boolean deleteCustomer(UUID id);
    List<Customer> searchCustomers(String searchTerm, SearchStrategy searchStrategy);
}