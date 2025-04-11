package id.ac.ui.cs.advprog.pelangganservice.command;

import id.ac.ui.cs.advprog.pelangganservice.model.Customer;
import id.ac.ui.cs.advprog.pelangganservice.repository.CustomerRepository;

public class ReadCustomerCommand {

    private final CustomerRepository repository;

    public ReadCustomerCommand(CustomerRepository repository) {
        this.repository = repository;
    }

    /**
     * Mencari dan mengembalikan Customer berdasarkan id.
     * Jika tidak ditemukan, melempar IllegalArgumentException.
     *
     * @param customerId id customer yang ingin dibaca.
     * @return Customer yang ditemukan.
     */
    public Customer execute(String customerId) {
        Customer customer = repository.findById(customerId);
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found");
        }
        return customer;
    }
}
