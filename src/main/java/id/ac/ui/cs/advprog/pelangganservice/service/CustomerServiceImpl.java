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

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository; // Sekarang ini adalah Spring Data JPA Repository

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    @Transactional // Penting untuk operasi tulis (create, update, delete)
    public Customer createCustomer(Customer customer) {
        // ID akan di-generate jika null sebelum persist (jika tidak pakai @GeneratedValue dan di-set manual)
        // atau bisa di-set di sini jika perlu logika khusus sebelum command
        if (customer.getId() == null) {
            customer.setId(UUID.randomUUID());
        }
        // createdAt dan updatedAt akan di-handle oleh @CreationTimestamp dan @UpdateTimestamp
        // jadi tidak perlu di-set di sini lagi.
        // customer.setCreatedAt(LocalDateTime.now());
        // customer.setUpdatedAt(LocalDateTime.now());

        // isActive tetap penting di-set di sini sebagai default untuk customer baru
        customer.setActive(true);

        // Command pattern tetap bisa digunakan.
        // CreateCustomerCommand akan memanggil customerRepository.save(customer)
        CreateCustomerCommand command = new CreateCustomerCommand(customerRepository, customer);
        return command.execute();
        // Atau langsung:
        // return customerRepository.save(customer);
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
    public Optional<Customer> updateCustomer(UUID id, Customer customerDetails) {
        // Pastikan ID di customerDetails sesuai dengan ID parameter
        customerDetails.setId(id); // Ini penting untuk UpdateCustomerCommand

        // UpdateCustomerCommand akan mengambil customer yang ada, memperbarui field,
        // dan memanggil customerRepository.save().
        // updatedAt akan di-handle oleh @UpdateTimestamp.
        UpdateCustomerCommand command = new UpdateCustomerCommand(customerRepository, customerDetails);
        return command.execute();
        // Atau logika update bisa langsung di sini jika tidak pakai command:
        // return customerRepository.findById(id).map(existingCustomer -> {
        //     if (customerDetails.getFullName() != null) {
        //         existingCustomer.setFullName(customerDetails.getFullName());
        //     }
        //     if (customerDetails.getPhoneNumber() != null) {
        //         existingCustomer.setPhoneNumber(customerDetails.getPhoneNumber());
        //     }
        //     if (customerDetails.getEmail() != null) {
        //         existingCustomer.setEmail(customerDetails.getEmail());
        //     }
        //     if (customerDetails.getAddress() != null) {
        //         existingCustomer.setAddress(customerDetails.getAddress());
        //     }
        //     // existingCustomer.setUpdatedAt(LocalDateTime.now()); // Tidak perlu jika pakai @UpdateTimestamp
        //     return customerRepository.save(existingCustomer);
        // });
    }

    @Override
    @Transactional
    public boolean deleteCustomer(UUID id) {
        // DeleteCustomerCommand akan memeriksa apakah ada dan memanggil deleteById.
        DeleteCustomerCommand command = new DeleteCustomerCommand(customerRepository, id);
        return command.execute();
        // Atau langsung:
        // if (customerRepository.existsById(id)) {
        //     customerRepository.deleteById(id);
        //     return true;
        // }
        // return false;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Customer> searchCustomers(String searchTerm, SearchStrategy searchStrategy) {
        // Strategi search masih akan bekerja pada List<Customer> yang diambil dari repository.
        // Untuk performa yang lebih baik pada database besar, Anda mungkin ingin
        // memindahkan logika search ke dalam query JPA (misalnya dengan Spesifikasi JPA atau Querydsl),
        // tetapi untuk saat ini, pendekatan yang ada akan berfungsi.
        List<Customer> allCustomers = customerRepository.findAll();
        return searchStrategy.search(allCustomers, searchTerm);
    }
}