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

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponseDTO> updateCustomer(@PathVariable UUID id,
                                                              @RequestBody /*@Valid*/ UpdateCustomerRequestDTO requestDTO) {
        // Service layer mungkin perlu dimodifikasi untuk menerima DTO atau
        // tetap menerima Customer entity yang sudah di-map sebagian.
        // Untuk saat ini, kita akan map DTO ke entitas Customer untuk update.
        // Namun, pendekatan yang lebih baik adalah service layer yang menerima DTO update.

        // Pendekatan 1: Service layer menerima entitas, controller melakukan mapping parsial
        // Ini kurang ideal karena controller jadi tahu cara mengupdate entitas.
        // Optional<Customer> customerOpt = customerService.getCustomerById(id);
        // if (customerOpt.isEmpty()) {
        //     return ResponseEntity.notFound().build();
        // }
        // Customer customerToUpdate = customerOpt.get();
        // CustomerMapper.updateEntityFromDTO(requestDTO, customerToUpdate);
        // Customer updatedCustomerEntity = customerService.saveCustomer(customerToUpdate); // Perlu metode save di service

        // Pendekatan 2: Service layer memiliki metode update yang menerima DTO atau field-field spesifik.
        // Mari kita asumsikan service tetap menerima Customer object yang fieldnya sudah di-set untuk diubah.
        // Ini berarti kita perlu cara untuk membuat Customer object dari DTO untuk update.
        // Idealnya, service.updateCustomer(UUID id, UpdateCustomerRequestDTO dto)

        // Untuk menjaga service signature saat ini (menerima Customer object untuk detail update):
        // Kita bisa buat Customer object dari DTO, tapi ini tidak ideal karena
        // field yang null di DTO bisa menimpa field yang sudah ada di entitas jika tidak hati-hati.
        // Mari modifikasi service untuk menerima UpdateCustomerRequestDTO.

        // Untuk sementara, jika service tetap menerima `Customer customerDetails`:
        // Buat objek Customer dari DTO, pastikan ID-nya adalah `id` dari path
        Customer customerDetailsForService = Customer.builder().id(id).build(); // ID penting
        CustomerMapper.updateEntityFromDTO(requestDTO, customerDetailsForService);
        // Perhatian: customerDetailsForService ini hanya berisi field dari DTO.
        // UpdateCustomerCommand Anda sudah menangani pengambilan entitas asli dan update field.

        Optional<Customer> updatedCustomerOpt = customerService.updateCustomer(id, customerDetailsForService);

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