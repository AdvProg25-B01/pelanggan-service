package id.ac.ui.cs.advprog.pelangganservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
// Import DTOs yang digunakan
import id.ac.ui.cs.advprog.pelangganservice.dto.CreateCustomerRequestDTO;
import id.ac.ui.cs.advprog.pelangganservice.dto.CustomerResponseDTO;
import id.ac.ui.cs.advprog.pelangganservice.dto.UpdateCustomerRequestDTO;
import id.ac.ui.cs.advprog.pelangganservice.model.Customer;
import id.ac.ui.cs.advprog.pelangganservice.service.CustomerService;
import id.ac.ui.cs.advprog.pelangganservice.strategy.SearchByFullNameStrategy;
// Hapus import SearchStrategy jika tidak digunakan langsung di sini
// import id.ac.ui.cs.advprog.pelangganservice.strategy.SearchStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @Autowired
    private ObjectMapper objectMapper;

    // Objek DTO untuk request dan response yang diharapkan
    private CreateCustomerRequestDTO createRequestDTO;
    private UpdateCustomerRequestDTO updateRequestDTO;
    private CustomerResponseDTO customer1ResponseDTO;
    private CustomerResponseDTO customer2ResponseDTO;

    private Customer customer1Entity; // Entitas untuk mocking service layer jika perlu
    private Customer customer2Entity;
    private UUID customer1Id;

    @BeforeEach
    void setUp() {
        customer1Id = UUID.randomUUID();

        // Data Entitas (untuk disimulasikan dikembalikan oleh service)
        customer1Entity = Customer.builder()
                .id(customer1Id)
                .fullName("Alice Smith")
                .email("alice@example.com")
                .phoneNumber("081234567890")
                .address("123 Wonderland")
                .isActive(true)
                .createdAt(LocalDateTime.now().minusDays(1)) // Beri nilai spesifik untuk createdAt
                .updatedAt(LocalDateTime.now().minusHours(1)) // Beri nilai spesifik untuk updatedAt
                .build();

        customer2Entity = Customer.builder()
                .id(UUID.randomUUID())
                .fullName("Bob Johnson")
                .email("bob@example.com")
                .phoneNumber("089876543210")
                .address("456 Builderland")
                .isActive(true)
                .createdAt(LocalDateTime.now().minusDays(2))
                .updatedAt(LocalDateTime.now().minusHours(2))
                .build();

        // Data DTO Response (yang diharapkan dikirim ke klien)
        customer1ResponseDTO = CustomerResponseDTO.builder()
                .id(customer1Entity.getId())
                .fullName(customer1Entity.getFullName())
                .email(customer1Entity.getEmail())
                .phoneNumber(customer1Entity.getPhoneNumber())
                .address(customer1Entity.getAddress())
                .isActive(customer1Entity.isActive())
                .createdAt(customer1Entity.getCreatedAt())
                .updatedAt(customer1Entity.getUpdatedAt())
                .build();

        customer2ResponseDTO = CustomerResponseDTO.builder()
                .id(customer2Entity.getId())
                .fullName(customer2Entity.getFullName())
                .email(customer2Entity.getEmail())
                .phoneNumber(customer2Entity.getPhoneNumber())
                .address(customer2Entity.getAddress())
                .isActive(customer2Entity.isActive())
                .createdAt(customer2Entity.getCreatedAt())
                .updatedAt(customer2Entity.getUpdatedAt())
                .build();

        // Data DTO Request (yang dikirim dari klien)
        createRequestDTO = CreateCustomerRequestDTO.builder()
                .fullName("New Customer")
                .email("new@example.com")
                .phoneNumber("111222333")
                .address("New Address")
                .build();

        updateRequestDTO = UpdateCustomerRequestDTO.builder()
                .fullName("Alice Wonderland")
                .email("alice.updated@example.com") // Contoh update email
                // Biarkan phoneNumber dan address null untuk menguji update parsial
                .build();
    }

    @Test
    void testCreateCustomer() throws Exception {
        // Entitas yang disimulasikan dibuat oleh service
        Customer createdEntity = Customer.builder()
                .id(UUID.randomUUID()) // Service akan set ID
                .fullName(createRequestDTO.getFullName())
                .email(createRequestDTO.getEmail())
                .phoneNumber(createRequestDTO.getPhoneNumber())
                .address(createRequestDTO.getAddress())
                .isActive(true) // Service akan set isActive
                .createdAt(LocalDateTime.now()) // Service (via JPA) akan set createdAt
                .updatedAt(LocalDateTime.now()) // Service (via JPA) akan set updatedAt
                .build();

        // DTO Response yang diharapkan dari controller
        CustomerResponseDTO expectedResponseDTO = CustomerResponseDTO.builder()
                .id(createdEntity.getId())
                .fullName(createdEntity.getFullName())
                .email(createdEntity.getEmail())
                .phoneNumber(createdEntity.getPhoneNumber())
                .address(createdEntity.getAddress())
                .isActive(createdEntity.isActive())
                .createdAt(createdEntity.getCreatedAt())
                .updatedAt(createdEntity.getUpdatedAt())
                .build();

        // Mock service untuk menerima Customer entity (karena service.createCustomer masih menerima Customer)
        // dan mengembalikan entitas yang sudah "disimpan"
        when(customerService.createCustomer(any(Customer.class))).thenReturn(createdEntity);

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequestDTO))) // Kirim CreateCustomerRequestDTO
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(expectedResponseDTO.getId().toString())))
                .andExpect(jsonPath("$.fullName", is(expectedResponseDTO.getFullName())))
                .andExpect(jsonPath("$.email", is(expectedResponseDTO.getEmail())))
                .andExpect(jsonPath("$.active", is(expectedResponseDTO.isActive())))
                // createdAt dan updatedAt bisa jadi sulit di-assert nilainya secara presisi
                // karena LocalDateTime.now() akan berbeda. Cukup pastikan fieldnya ada.
                .andExpect(jsonPath("$.createdAt").exists())
                .andExpect(jsonPath("$.updatedAt").exists())
                .andExpect(header().exists("Location"));
    }

    @Test
    void testGetAllCustomers_noParams() throws Exception {
        List<Customer> allCustomerEntities = Arrays.asList(customer1Entity, customer2Entity);
        when(customerService.getAllCustomers()).thenReturn(allCustomerEntities);

        List<CustomerResponseDTO> expectedResponseDTOs = Arrays.asList(customer1ResponseDTO, customer2ResponseDTO);

        mockMvc.perform(get("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(expectedResponseDTOs.get(0).getId().toString())))
                .andExpect(jsonPath("$[0].fullName", is(expectedResponseDTOs.get(0).getFullName())))
                .andExpect(jsonPath("$[1].id", is(expectedResponseDTOs.get(1).getId().toString())))
                .andExpect(jsonPath("$[1].fullName", is(expectedResponseDTOs.get(1).getFullName())));
    }

    @Test
    void testSearchCustomers_byFullName() throws Exception {
        List<Customer> searchResultEntities = Collections.singletonList(customer1Entity);
        // Pastikan mock untuk searchCustomers menggunakan strategy yang sesuai dengan yang dibuat di controller
        when(customerService.searchCustomers(eq("Alice"), any(SearchByFullNameStrategy.class)))
                .thenReturn(searchResultEntities);

        List<CustomerResponseDTO> expectedResponseDTOs = Collections.singletonList(customer1ResponseDTO);

        mockMvc.perform(get("/api/customers")
                        .param("searchTerm", "Alice")
                        .param("searchBy", "fullName")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(expectedResponseDTOs.get(0).getId().toString())))
                .andExpect(jsonPath("$[0].fullName", is(expectedResponseDTOs.get(0).getFullName())));
    }

    @Test
    void testSearchCustomers_invalidSearchBy() throws Exception {
        mockMvc.perform(get("/api/customers")
                        .param("searchTerm", "Alice")
                        .param("searchBy", "invalidField")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }


    @Test
    void testGetCustomerById_found() throws Exception {
        when(customerService.getCustomerById(customer1Id)).thenReturn(Optional.of(customer1Entity));

        mockMvc.perform(get("/api/customers/{id}", customer1Id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(customer1ResponseDTO.getId().toString())))
                .andExpect(jsonPath("$.fullName", is(customer1ResponseDTO.getFullName())))
                .andExpect(jsonPath("$.email", is(customer1ResponseDTO.getEmail())));
    }

    @Test
    void testGetCustomerById_notFound() throws Exception {
        UUID notFoundId = UUID.randomUUID();
        when(customerService.getCustomerById(notFoundId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/customers/{id}", notFoundId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateCustomer_found() throws Exception {
        // updateRequestDTO sudah di-setup di @BeforeEach
        // updateRequestDTO: fullName="Alice Wonderland", email="alice.updated@example.com"

        // Entitas yang disimulasikan setelah update oleh service
        Customer customerEntityAfterUpdate = Customer.builder()
                .id(customer1Id)
                .fullName(updateRequestDTO.getFullName()) // "Alice Wonderland"
                .email(updateRequestDTO.getEmail())       // "alice.updated@example.com"
                .phoneNumber(customer1Entity.getPhoneNumber()) // Nomor telepon tidak diubah di DTO, jadi tetap
                .address(customer1Entity.getAddress())         // Alamat tidak diubah di DTO, jadi tetap
                .isActive(customer1Entity.isActive())          // isActive tidak diubah oleh DTO ini (kecuali Anda menambahkannya)
                .createdAt(customer1Entity.getCreatedAt())     // createdAt tidak berubah
                .updatedAt(LocalDateTime.now().plusHours(1)) // updatedAt akan berubah
                .build();

        // DTO Response yang diharapkan
        CustomerResponseDTO expectedResponseDTO = CustomerResponseDTO.builder()
                .id(customerEntityAfterUpdate.getId())
                .fullName(customerEntityAfterUpdate.getFullName())
                .email(customerEntityAfterUpdate.getEmail())
                .phoneNumber(customerEntityAfterUpdate.getPhoneNumber())
                .address(customerEntityAfterUpdate.getAddress())
                .isActive(customerEntityAfterUpdate.isActive())
                .createdAt(customerEntityAfterUpdate.getCreatedAt())
                .updatedAt(customerEntityAfterUpdate.getUpdatedAt())
                .build();

        // Penting: Mock customerService.updateCustomer untuk menerima UpdateCustomerRequestDTO
        when(customerService.updateCustomer(eq(customer1Id), any(UpdateCustomerRequestDTO.class)))
                .thenReturn(Optional.of(customerEntityAfterUpdate));

        mockMvc.perform(put("/api/customers/{id}", customer1Id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequestDTO))) // Kirim UpdateCustomerRequestDTO
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(expectedResponseDTO.getId().toString())))
                .andExpect(jsonPath("$.fullName", is(expectedResponseDTO.getFullName())))
                .andExpect(jsonPath("$.email", is(expectedResponseDTO.getEmail())))
                .andExpect(jsonPath("$.active", is(expectedResponseDTO.isActive()))) // Asumsikan active tidak berubah jika tidak di DTO
                .andExpect(jsonPath("$.updatedAt").exists()); // Cek updatedAt ada
    }

    @Test
    void testUpdateCustomer_notFound() throws Exception {
        UUID notFoundId = UUID.randomUUID();
        UpdateCustomerRequestDTO nonExistentUpdateRequestDTO = UpdateCustomerRequestDTO.builder()
                .fullName("Non Existent Update").build();

        // Mock service untuk menerima UpdateCustomerRequestDTO
        when(customerService.updateCustomer(eq(notFoundId), any(UpdateCustomerRequestDTO.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(put("/api/customers/{id}", notFoundId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nonExistentUpdateRequestDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteCustomer_found() throws Exception {
        when(customerService.deleteCustomer(customer1Id)).thenReturn(true);

        mockMvc.perform(delete("/api/customers/{id}", customer1Id))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteCustomer_notFound() throws Exception {
        UUID notFoundId = UUID.randomUUID();
        when(customerService.deleteCustomer(notFoundId)).thenReturn(false);

        mockMvc.perform(delete("/api/customers/{id}", notFoundId))
                .andExpect(status().isNotFound());
    }
}