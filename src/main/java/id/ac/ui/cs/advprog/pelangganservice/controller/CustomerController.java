package id.ac.ui.cs.advprog.pelangganservice.controller;

import id.ac.ui.cs.advprog.pelangganservice.model.Customer;
import id.ac.ui.cs.advprog.pelangganservice.service.CustomerService;
import id.ac.ui.cs.advprog.pelangganservice.strategy.SearchByEmailStrategy;
import id.ac.ui.cs.advprog.pelangganservice.strategy.SearchByFullNameStrategy;
import id.ac.ui.cs.advprog.pelangganservice.strategy.SearchByPhoneNumberStrategy;
import id.ac.ui.cs.advprog.pelangganservice.strategy.SearchStrategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/customers") // Base path untuk semua endpoint customer
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    // Create a new customer (C)    201 (created) (resource baru)
    @PostMapping
    public ResponseEntity<Customer> createCustomer(@RequestBody Customer customer) {
        // ID, createdAt, updatedAt, dan isActive akan di-handle oleh service/repository
        Customer createdCustomer = customerService.createCustomer(customer);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdCustomer.getId())
                .toUri();
        return ResponseEntity.created(location).body(createdCustomer);
    }

    // Get all customers or search customers (R)
    @GetMapping
    public ResponseEntity<List<Customer>> getAllOrSearchCustomers(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) String searchBy // "fullName", "email", "phoneNumber"
    ) {
        List<Customer> customers;
        if (searchTerm != null && !searchTerm.isEmpty() && searchBy != null && !searchBy.isEmpty()) {
            SearchStrategy strategy;
            switch (searchBy.toLowerCase()) {
                case "fullname":
                    strategy = new SearchByFullNameStrategy();
                    break;
                case "email":
                    strategy = new SearchByEmailStrategy();
                    break;
                case "phonenumber":
                    strategy = new SearchByPhoneNumberStrategy();
                    break;
                default:
                    // Atau return bad request jika searchBy tidak valid
                    return ResponseEntity.badRequest().body(null); // Atau list kosong dengan pesan
            }
            customers = customerService.searchCustomers(searchTerm, strategy);
        } else {
            customers = customerService.getAllCustomers();
        }
        return ResponseEntity.ok(customers);
    }

    // Get a single customer by ID (R) 200 (permintaan sukses)
    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable UUID id) {
        Optional<Customer> customer = customerService.getCustomerById(id);
        return customer.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Update an existing customer (U)
    @PutMapping("/{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable UUID id, @RequestBody Customer customerDetails) {
        Optional<Customer> updatedCustomer = customerService.updateCustomer(id, customerDetails);
        return updatedCustomer.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Delete a customer (D)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable UUID id) {
        boolean deleted = customerService.deleteCustomer(id);
        if (deleted) {
            return ResponseEntity.noContent().build(); // 204 untk operasi hapus tanpa isi response
        } else {
            return ResponseEntity.notFound().build(); // 404 data tidak ditemukan
        }
    }
}