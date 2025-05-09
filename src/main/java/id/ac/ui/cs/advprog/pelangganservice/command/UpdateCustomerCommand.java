package id.ac.ui.cs.advprog.pelangganservice.command;

import id.ac.ui.cs.advprog.pelangganservice.model.Customer;
import id.ac.ui.cs.advprog.pelangganservice.repository.CustomerRepository;
import java.time.LocalDateTime;
import java.util.Optional;

public class UpdateCustomerCommand implements Command<Optional<Customer>> {
    private final CustomerRepository customerRepository;
    private final Customer customerToUpdate; // Ini berisi data baru, termasuk ID yang sudah ada

    public UpdateCustomerCommand(CustomerRepository customerRepository, Customer customerToUpdate) {
        this.customerRepository = customerRepository;
        this.customerToUpdate = customerToUpdate;
    }

    @Override
    public Optional<Customer> execute() {
        Optional<Customer> existingCustomerOpt = customerRepository.findById(customerToUpdate.getId());
        if (existingCustomerOpt.isPresent()) {
            Customer existingCustomer = existingCustomerOpt.get();
            // Update fields
            existingCustomer.setFullName(customerToUpdate.getFullName());
            existingCustomer.setPhoneNumber(customerToUpdate.getPhoneNumber());
            existingCustomer.setEmail(customerToUpdate.getEmail());
            existingCustomer.setAddress(customerToUpdate.getAddress());
            existingCustomer.setActive(customerToUpdate.isActive());
            // updatedAt akan di-set oleh repository.save()
            return Optional.of(customerRepository.save(existingCustomer));
        }
        return Optional.empty(); // Customer not found
    }
}