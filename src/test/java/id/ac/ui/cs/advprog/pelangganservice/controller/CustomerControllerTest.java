package id.ac.ui.cs.advprog.pelangganservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import id.ac.ui.cs.advprog.pelangganservice.model.Customer;
import id.ac.ui.cs.advprog.pelangganservice.service.CustomerService;
import id.ac.ui.cs.advprog.pelangganservice.strategy.SearchByFullNameStrategy;
import id.ac.ui.cs.advprog.pelangganservice.strategy.SearchStrategy;
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

@WebMvcTest(CustomerController.class) // Fokus hanya pada CustomerController
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean // Membuat mock dari CustomerService
    private CustomerService customerService;

    @Autowired
    private ObjectMapper objectMapper; // Untuk konversi Java Object ke JSON String

    private Customer customer1;
    private Customer customer2;
    private UUID customer1Id;

    @BeforeEach
    void setUp() {
        customer1Id = UUID.randomUUID();
        customer1 = Customer.builder()
                .id(customer1Id)
                .fullName("Alice Smith")
                .email("alice@example.com")
                .phoneNumber("081234567890")
                .address("123 Wonderland")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        customer2 = Customer.builder()
                .id(UUID.randomUUID())
                .fullName("Bob Johnson")
                .email("bob@example.com")
                .phoneNumber("089876543210")
                .address("456 Builderland")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    void testCreateCustomer() throws Exception {
        Customer customerToCreate = Customer.builder()
                .fullName("New Customer")
                .email("new@example.com")
                .phoneNumber("111222333")
                .address("New Address")
                .build(); // isActive, id, timestamps akan diset service

        // Customer yang diharapkan setelah disave (dengan ID, timestamps, dll)
        Customer createdCustomer = Customer.builder()
                .id(UUID.randomUUID())
                .fullName(customerToCreate.getFullName())
                .email(customerToCreate.getEmail())
                .phoneNumber(customerToCreate.getPhoneNumber())
                .address(customerToCreate.getAddress())
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(customerService.createCustomer(any(Customer.class))).thenReturn(createdCustomer);

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(customerToCreate)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(createdCustomer.getId().toString())))
                .andExpect(jsonPath("$.fullName", is(createdCustomer.getFullName())))
                .andExpect(header().exists("Location"));
    }

    @Test
    void testGetAllCustomers_noParams() throws Exception {
        List<Customer> allCustomers = Arrays.asList(customer1, customer2);
        when(customerService.getAllCustomers()).thenReturn(allCustomers);

        mockMvc.perform(get("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].fullName", is(customer1.getFullName())))
                .andExpect(jsonPath("$[1].fullName", is(customer2.getFullName())));
    }

    @Test
    void testSearchCustomers_byFullName() throws Exception {
        List<Customer> searchResult = Collections.singletonList(customer1);
        when(customerService.searchCustomers(eq("Alice"), any(SearchByFullNameStrategy.class)))
                .thenReturn(searchResult);

        mockMvc.perform(get("/api/customers")
                        .param("searchTerm", "Alice")
                        .param("searchBy", "fullName")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].fullName", is(customer1.getFullName())));
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
        when(customerService.getCustomerById(customer1Id)).thenReturn(Optional.of(customer1));

        mockMvc.perform(get("/api/customers/{id}", customer1Id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(customer1Id.toString())))
                .andExpect(jsonPath("$.fullName", is(customer1.getFullName())));
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
        Customer updatedDetails = Customer.builder()
                .fullName("Alice Wonderland") // Nama diupdate
                .email(customer1.getEmail()) // Email tetap
                .phoneNumber(customer1.getPhoneNumber())
                .address(customer1.getAddress())
                .isActive(false) // Status diupdate
                .build();

        Customer customerAfterUpdate = Customer.builder()
                .id(customer1Id)
                .fullName(updatedDetails.getFullName())
                .email(updatedDetails.getEmail())
                .phoneNumber(updatedDetails.getPhoneNumber())
                .address(updatedDetails.getAddress())
                .isActive(updatedDetails.isActive())
                .createdAt(customer1.getCreatedAt()) // createdAt tidak berubah
                .updatedAt(LocalDateTime.now().plusSeconds(1)) // updatedAt berubah
                .build();


        when(customerService.updateCustomer(eq(customer1Id), any(Customer.class)))
                .thenReturn(Optional.of(customerAfterUpdate));

        mockMvc.perform(put("/api/customers/{id}", customer1Id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDetails)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName", is(customerAfterUpdate.getFullName())))
                .andExpect(jsonPath("$.isActive", is(customerAfterUpdate.isActive())));
    }

    @Test
    void testUpdateCustomer_notFound() throws Exception {
        UUID notFoundId = UUID.randomUUID();
        Customer updatedDetails = Customer.builder().fullName("Non Existent").build();
        when(customerService.updateCustomer(eq(notFoundId), any(Customer.class)))
                .thenReturn(Optional.empty());

        mockMvc.perform(put("/api/customers/{id}", notFoundId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedDetails)))
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