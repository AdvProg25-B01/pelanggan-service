package id.ac.ui.cs.advprog.pelangganservice.command;

import id.ac.ui.cs.advprog.pelangganservice.model.Customer;
import id.ac.ui.cs.advprog.pelangganservice.repository.CustomerRepository;

public class UpdateCustomerCommand {

    private final CustomerRepository repository;

    public UpdateCustomerCommand(CustomerRepository repository) {
        this.repository = repository;
    }

    public void execute(Customer updatedCustomer) {
        // Cek apakah ID tidak ditemukan di repository
        Customer existing = repository.findById(updatedCustomer.getId());
        if (existing == null) {
            throw new IllegalArgumentException("Customer not found");
        }

        // Validasi nama
        String newName = updatedCustomer.getName();
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer name cannot be empty");
        }

        // Lakukan penyimpanan (update)
        repository.save(updatedCustomer);
    }
}
