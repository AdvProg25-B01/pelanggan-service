package id.ac.ui.cs.advprog.pelangganservice.service;

import id.ac.ui.cs.advprog.pelangganservice.command.CreateCustomerCommand;
import id.ac.ui.cs.advprog.pelangganservice.command.DeleteCustomerCommand;
import id.ac.ui.cs.advprog.pelangganservice.command.UpdateCustomerCommand;
import id.ac.ui.cs.advprog.pelangganservice.model.Customer;
import id.ac.ui.cs.advprog.pelangganservice.repository.CustomerRepository;
import id.ac.ui.cs.advprog.pelangganservice.strategy.SearchStrategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Import untuk anotasi transaksi

import java.time.LocalDateTime; // Mungkin tidak perlu lagi jika pakai @CreationTimestamp/@UpdateTimestamp
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import id.ac.ui.cs.advprog.pelangganservice.dto.UpdateCustomerRequestDTO;
import id.ac.ui.cs.advprog.pelangganservice.dto.CustomerMapper;

import org.slf4j.Logger; // Untuk logging
import org.slf4j.LoggerFactory; // Untuk logging
import org.springframework.scheduling.annotation.Async; // Import @Async
import java.util.concurrent.CompletableFuture; // Import CompletableFuture

@Service
public class CustomerServiceImpl implements CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerServiceImpl.class); // Logger

    private final CustomerRepository customerRepository; // Sekarang ini adalah Spring Data JPA Repository

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    @Transactional // Penting untuk operasi tulis (create, update, delete)
    public Customer createCustomer(Customer customer) {
        if (customer.getId() == null) {
            customer.setId(UUID.randomUUID());
        }
        customer.setActive(true);

        CreateCustomerCommand command = new CreateCustomerCommand(customerRepository, customer);
        Customer createdCustomer = command.execute(); // Simpan Customer dulu

        // Setelah customer berhasil disimpan, panggil tugas asinkron
        if (createdCustomer != null) {
            sendWelcomeEmailAsync(createdCustomer);
            logCustomerCreationAuditEventAsync(createdCustomer.getId(), "CREATE_CUSTOMER_SUCCESS");
        }

        return createdCustomer;
    }

    // Metode Asinkron untuk mengirim email (simulasi)
    @Async
    public CompletableFuture<Void> sendWelcomeEmailAsync(Customer customer) {
        logger.info("Starting to send welcome email to: {} (ID: {}) on thread: {}",
                customer.getEmail(), customer.getId(), Thread.currentThread().getName());
        try {
            // Simulasi proses pengiriman email yang memakan waktu
            Thread.sleep(3000); // Tunggu 3 detik
            logger.info("Successfully sent welcome email to: {}", customer.getEmail());
        } catch (InterruptedException e) {
            logger.error("Email sending was interrupted for customer: {}", customer.getEmail(), e);
            Thread.currentThread().interrupt(); // Set interrupt flag
        }
        return CompletableFuture.completedFuture(null); // Kembalikan Future yang sudah selesai (untuk tipe void)
    }

    // Metode Asinkron untuk logging audit (simulasi)
    @Async
    public CompletableFuture<Void> logCustomerCreationAuditEventAsync(UUID customerId, String eventType) {
        logger.info("Starting audit logging for customer ID: {} - Event: {} on thread: {}",
                customerId, eventType, Thread.currentThread().getName());
        try {
            // Simulasi proses logging ke sistem audit eksternal
            Thread.sleep(1000); // Tunggu 1 detik
            logger.info("Successfully logged audit event for customer ID: {} - Event: {}", customerId, eventType);
        } catch (InterruptedException e) {
            logger.error("Audit logging was interrupted for customer ID: {}", customerId, e);
            Thread.currentThread().interrupt();
        }
        return CompletableFuture.completedFuture(null);
    }

    @Override
    @Transactional(readOnly = true) // Baik untuk operasi baca
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Customer> getCustomerById(UUID id) {
        return customerRepository.findById(id);
    }

    @Override
    @Transactional
    public Optional<Customer> updateCustomer(UUID id, UpdateCustomerRequestDTO customerDetailsDTO) {
        Optional<Customer> customerOpt = customerRepository.findById(id);
        if (customerOpt.isEmpty()) {
            return Optional.empty();
        }
        Customer customerToUpdate = customerOpt.get();
        CustomerMapper.updateEntityFromDTO(customerDetailsDTO, customerToUpdate);
        return Optional.of(customerRepository.save(customerToUpdate));
    }

    @Override
    @Transactional
    public boolean deleteCustomer(UUID id) {
        DeleteCustomerCommand command = new DeleteCustomerCommand(customerRepository, id);
        return command.execute();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Customer> searchCustomers(String searchTerm, SearchStrategy searchStrategy) {
        List<Customer> allCustomers = customerRepository.findAll();
        return searchStrategy.search(allCustomers, searchTerm);
    }
}