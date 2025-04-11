package id.ac.ui.cs.advprog.pelangganservice.command;

import id.ac.ui.cs.advprog.pelangganservice.model.Customer;
import id.ac.ui.cs.advprog.pelangganservice.repository.CustomerRepository;

public class AddCustomerCommand {

    private final CustomerRepository repository;

    public AddCustomerCommand(CustomerRepository repository) {
        this.repository = repository;
    }

    public void execute(Customer customer) {
        // Validasi minimal: cek apakah nama customer kosong
        if (customer.getName() == null || customer.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be empty");
        }
        // Lakukan operasi penyimpanan
        repository.save(customer);
    }
}
