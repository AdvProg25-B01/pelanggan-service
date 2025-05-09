package id.ac.ui.cs.advprog.pelangganservice.service;

import id.ac.ui.cs.advprog.pelangganservice.command.CreateCustomerCommand;
import id.ac.ui.cs.advprog.pelangganservice.command.DeleteCustomerCommand;
import id.ac.ui.cs.advprog.pelangganservice.command.UpdateCustomerCommand;
import id.ac.ui.cs.advprog.pelangganservice.model.Customer;
import id.ac.ui.cs.advprog.pelangganservice.repository.CustomerRepository;
import id.ac.ui.cs.advprog.pelangganservice.strategy.SearchStrategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Customer createCustomer(Customer customer) {
        // Logika default value untuk customer baru
        if (customer.getId() == null) { // Pastikan ID baru jika belum ada
            customer.setId(UUID.randomUUID());
        }
        customer.setCreatedAt(LocalDateTime.now());
        customer.setUpdatedAt(LocalDateTime.now());
        customer.setActive(true); // Default pelanggan baru aktif

        // Menggunakan Command Pattern untuk Create
        CreateCustomerCommand command = new CreateCustomerCommand(customerRepository, customer);
        return command.execute();
    }

    @Override
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public Optional<Customer> getCustomerById(UUID id) {
        return customerRepository.findById(id);
    }

    @Override
    public Optional<Customer> updateCustomer(UUID id, Customer customerDetails) {
        // Pastikan ID di customerDetails sesuai dengan ID parameter
        customerDetails.setId(id);

        // Menggunakan Command Pattern untuk Update
        // Command akan menghandle pengecekan apakah customer ada dan melakukan update
        UpdateCustomerCommand command = new UpdateCustomerCommand(customerRepository, customerDetails);
        return command.execute();
    }

    @Override
    public boolean deleteCustomer(UUID id) {
        // Menggunakan Command Pattern untuk Delete
        DeleteCustomerCommand command = new DeleteCustomerCommand(customerRepository, id);
        return command.execute();
    }

    @Override
    public List<Customer> searchCustomers(String searchTerm, SearchStrategy searchStrategy) {
        List<Customer> allCustomers = customerRepository.findAll();
        return searchStrategy.search(allCustomers, searchTerm);
    }
}