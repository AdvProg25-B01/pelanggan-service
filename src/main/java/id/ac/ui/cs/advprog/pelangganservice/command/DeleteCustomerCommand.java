package id.ac.ui.cs.advprog.pelangganservice.command;

import id.ac.ui.cs.advprog.pelangganservice.repository.CustomerRepository;
import java.util.UUID;

public class DeleteCustomerCommand implements Command<Boolean> { // Return true jika berhasil, false jika tidak
    private final CustomerRepository customerRepository;
    private final UUID customerId;

    public DeleteCustomerCommand(CustomerRepository customerRepository, UUID customerId) {
        this.customerRepository = customerRepository;
        this.customerId = customerId;
    }

    @Override
    public Boolean execute() {
        if (customerRepository.findById(customerId).isPresent()) {
            customerRepository.deleteById(customerId);
            return true;
        }
        return false; // Customer not found
    }
}