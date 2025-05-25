package id.ac.ui.cs.advprog.pelangganservice.service;

import id.ac.ui.cs.advprog.pelangganservice.model.Customer;
import id.ac.ui.cs.advprog.pelangganservice.strategy.SearchStrategy;
import id.ac.ui.cs.advprog.pelangganservice.dto.UpdateCustomerRequestDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CustomerService {
    Customer createCustomer(Customer customer); // Bisa juga menerima CreateCustomerRequestDTO
    List<Customer> getAllCustomers();
    Optional<Customer> getCustomerById(UUID id);
    Optional<Customer> updateCustomer(UUID id, UpdateCustomerRequestDTO customerDetailsDTO);
    boolean deleteCustomer(UUID id);
    List<Customer> searchCustomers(String searchTerm, SearchStrategy searchStrategy);
}