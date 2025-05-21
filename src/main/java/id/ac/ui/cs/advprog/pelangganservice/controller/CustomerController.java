package id.ac.ui.cs.advprog.pelangganservice.controller;

// Import DTOs dan Mapper
import id.ac.ui.cs.advprog.pelangganservice.dto.CreateCustomerRequestDTO;
import id.ac.ui.cs.advprog.pelangganservice.dto.CustomerMapper;
import id.ac.ui.cs.advprog.pelangganservice.dto.CustomerResponseDTO;
import id.ac.ui.cs.advprog.pelangganservice.dto.UpdateCustomerRequestDTO;

import id.ac.ui.cs.advprog.pelangganservice.model.Customer;
import id.ac.ui.cs.advprog.pelangganservice.service.CustomerService;
import id.ac.ui.cs.advprog.pelangganservice.strategy.SearchByEmailStrategy;
import id.ac.ui.cs.advprog.pelangganservice.strategy.SearchByFullNameStrategy;
import id.ac.ui.cs.advprog.pelangganservice.strategy.SearchByPhoneNumberStrategy;
import id.ac.ui.cs.advprog.pelangganservice.strategy.SearchStrategy;

import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus; // Mungkin tidak semua diperlukan lagi
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
// import jakarta.validation.Valid; // Jika menggunakan validasi

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    // Gunakan @Valid jika ada anotasi validasi di DTO
    public ResponseEntity<CustomerResponseDTO> createCustomer(@RequestBody /*@Valid*/ CreateCustomerRequestDTO requestDTO) {
        Customer customerToCreate = CustomerMapper.toEntity(requestDTO);
        Customer createdCustomer = customerService.createCustomer(customerToCreate);
        CustomerResponseDTO responseDTO = CustomerMapper.toResponseDTO(createdCustomer);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(responseDTO.getId()) // Gunakan ID dari DTO
                .toUri();
        return ResponseEntity.created(location).body(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponseDTO>> getAllOrSearchCustomers(
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) String searchBy) {

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
                    return ResponseEntity.badRequest().body(null);
            }
            customers = customerService.searchCustomers(searchTerm, strategy);
        } else {
            customers = customerService.getAllCustomers();
        }

        List<CustomerResponseDTO> responseDTOs = customers.stream()
                .map(CustomerMapper::toResponseDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseDTOs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> getCustomerById(@PathVariable UUID id) {
        Optional<Customer> customerOpt = customerService.getCustomerById(id);
        return customerOpt
                .map(CustomerMapper::toResponseDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Di CustomerController
    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> updateCustomer(@PathVariable UUID id,
                                                              @RequestBody /*@Valid*/ UpdateCustomerRequestDTO requestDTO) {
        Optional<Customer> updatedCustomerOpt = customerService.updateCustomer(id, requestDTO); // Langsung pass DTO
        return updatedCustomerOpt
                .map(CustomerMapper::toResponseDTO)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Metode Delete tetap sama karena tidak ada request/response body
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable UUID id) {
        boolean deleted = customerService.deleteCustomer(id);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}