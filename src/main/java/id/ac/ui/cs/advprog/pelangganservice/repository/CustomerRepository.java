package id.ac.ui.cs.advprog.pelangganservice.repository;

import id.ac.ui.cs.advprog.pelangganservice.model.Customer;

public interface CustomerRepository {
    void save(Customer customer);
    Customer findById(String id);
}
