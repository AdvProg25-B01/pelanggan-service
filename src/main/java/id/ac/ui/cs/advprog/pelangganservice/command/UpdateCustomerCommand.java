package id.ac.ui.cs.advprog.pelangganservice.command;

import id.ac.ui.cs.advprog.pelangganservice.model.Customer;
import id.ac.ui.cs.advprog.pelangganservice.repository.CustomerRepository;
import java.time.LocalDateTime;
import java.util.Optional;

public class UpdateCustomerCommand implements Command<Optional<Customer>> {
    private final CustomerRepository customerRepository;
    private final Customer customerDetailsFromRequest; // Mengganti nama agar lebih jelas

    public UpdateCustomerCommand(CustomerRepository customerRepository, Customer customerDetailsFromRequest) {
        this.customerRepository = customerRepository;
        this.customerDetailsFromRequest = customerDetailsFromRequest;
    }

    @Override
    public Optional<Customer> execute() {
        Optional<Customer> existingCustomerOpt = customerRepository.findById(customerDetailsFromRequest.getId());
        if (existingCustomerOpt.isPresent()) {
            Customer customerToUpdate = existingCustomerOpt.get(); // Ini adalah entitas dari DB

            // Update fields dari customerDetailsFromRequest ke customerToUpdate
            // Hanya update jika field di request tidak null (opsional, tergantung kebutuhan)
            // atau selalu update jika ada.
            if (customerDetailsFromRequest.getFullName() != null) {
                customerToUpdate.setFullName(customerDetailsFromRequest.getFullName());
            }
            if (customerDetailsFromRequest.getPhoneNumber() != null) {
                customerToUpdate.setPhoneNumber(customerDetailsFromRequest.getPhoneNumber());
            }
            if (customerDetailsFromRequest.getEmail() != null) {
                customerToUpdate.setEmail(customerDetailsFromRequest.getEmail());
            }
            if (customerDetailsFromRequest.getAddress() != null) {
                customerToUpdate.setAddress(customerDetailsFromRequest.getAddress());
            }

            // existingCustomer.setActive(customerDetailsFromRequest.isActive()); // Tetap dikomentari

            customerToUpdate.setUpdatedAt(LocalDateTime.now()); // Set updatedAt di sini

            return Optional.of(customerRepository.save(customerToUpdate));
        }
        return Optional.empty(); // Customer not found
    }
}