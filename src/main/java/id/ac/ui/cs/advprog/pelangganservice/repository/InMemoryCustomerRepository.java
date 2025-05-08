package id.ac.ui.cs.advprog.pelangganservice.repository;

import id.ac.ui.cs.advprog.pelangganservice.model.Customer;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class InMemoryCustomerRepository implements CustomerRepository {
    private final Map<UUID, Customer> customers = new ConcurrentHashMap<>();

    @Override
    public Customer save(Customer customer) {
        if (customer.getId() == null) {
            customer.setId(UUID.randomUUID());
            customer.setCreatedAt(LocalDateTime.now());
            customer.setActive(true); // Default active saat pembuatan baru
        }
        customer.setUpdatedAt(LocalDateTime.now());
        customers.put(customer.getId(), customer);
        return customer;
    }

    @Override
    public Optional<Customer> findById(UUID id) {
        return Optional.ofNullable(customers.get(id));
    }

    @Override
    public List<Customer> findAll() {
        return new ArrayList<>(customers.values());
    }

    @Override
    public void deleteById(UUID id) {
        customers.remove(id);
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        return customers.values().stream()
                .filter(customer -> customer.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }
}