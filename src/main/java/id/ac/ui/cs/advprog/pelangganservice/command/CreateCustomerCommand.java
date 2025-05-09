package id.ac.ui.cs.advprog.pelangganservice.command;

import id.ac.ui.cs.advprog.pelangganservice.model.Customer;
import id.ac.ui.cs.advprog.pelangganservice.repository.CustomerRepository;

public class CreateCustomerCommand implements Command<Customer> {
    private final CustomerRepository customerRepository;
    private final Customer customer;

    public CreateCustomerCommand(CustomerRepository customerRepository, Customer customer) {
        this.customerRepository = customerRepository;
        this.customer = customer;
    }

    @Override
    public Customer execute() {
        // Logika createdAt dan isActive dihandle di repository atau service
        return customerRepository.save(customer);
    }
}