package id.ac.ui.cs.advprog.pelangganservice.service;

import id.ac.ui.cs.advprog.pelangganservice.model.Customer;
import id.ac.ui.cs.advprog.pelangganservice.repository.CustomerRepository;
import id.ac.ui.cs.advprog.pelangganservice.strategy.SearchByFullNameStrategy;
import id.ac.ui.cs.advprog.pelangganservice.strategy.SearchStrategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerServiceImpl customerService;

    private Customer sampleCustomer;
    private UUID sampleCustomerId;

    @BeforeEach
    void setUp() {
        sampleCustomerId = UUID.randomUUID();
        sampleCustomer = Customer.builder()
                .id(sampleCustomerId)
                .fullName("Test Customer")
                .email("test@example.com")
                .phoneNumber("0123456789")
                .address("123 Test St")
                .isActive(true)
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now().minusHours(1))
                .build();
    }

    @Test
    void testCreateCustomer() {
        Customer newCustomerDetails = Customer.builder()
                .fullName("New User")
                .email("newuser@example.com")
                .phoneNumber("9876543210")
                .address("456 New Ave")
                .build(); // ID, timestamps, isActive will be set by service/repo

        // Mock repository.save to return the customer with generated ID and timestamps
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer c = invocation.getArgument(0);
            if (c.getId() == null) c.setId(UUID.randomUUID()); // Simulate ID generation if service didn't set it
            c.setCreatedAt(LocalDateTime.now()); // Simulate timestamp set by repo
            c.setUpdatedAt(LocalDateTime.now());
            c.setActive(true);
            return c;
        });

        Customer createdCustomer = customerService.createCustomer(newCustomerDetails);

        assertNotNull(createdCustomer.getId());
        assertEquals("New User", createdCustomer.getFullName());
        assertTrue(createdCustomer.isActive());
        assertNotNull(createdCustomer.getCreatedAt());
        assertNotNull(createdCustomer.getUpdatedAt());
        // Verify that repository.save was called via the command
        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void testCreateCustomer_setsTimestampsAndActive() {
        Customer customerToCreate = Customer.builder().fullName("Fresh Customer").email("fresh@example.com").build();

        // Mock the save operation to return the argument passed to it, but after service modifies it
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Customer result = customerService.createCustomer(customerToCreate);

        assertNotNull(result.getId()); // Service should generate ID if not present
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
        assertTrue(result.isActive());
        assertEquals(result.getCreatedAt(), result.getUpdatedAt()); // On creation, they should be very close or equal

        verify(customerRepository).save(result);
    }


    @Test
    void testGetAllCustomers() {
        List<Customer> customers = Arrays.asList(sampleCustomer, Customer.builder().id(UUID.randomUUID()).build());
        when(customerRepository.findAll()).thenReturn(customers);

        List<Customer> result = customerService.getAllCustomers();

        assertEquals(2, result.size());
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void testGetCustomerById_found() {
        when(customerRepository.findById(sampleCustomerId)).thenReturn(Optional.of(sampleCustomer));

        Optional<Customer> result = customerService.getCustomerById(sampleCustomerId);

        assertTrue(result.isPresent());
        assertEquals(sampleCustomer, result.get());
        verify(customerRepository, times(1)).findById(sampleCustomerId);
    }

    @Test
    void testGetCustomerById_notFound() {
        UUID notFoundId = UUID.randomUUID();
        when(customerRepository.findById(notFoundId)).thenReturn(Optional.empty());

        Optional<Customer> result = customerService.getCustomerById(notFoundId);

        assertFalse(result.isPresent());
        verify(customerRepository, times(1)).findById(notFoundId);
    }

    @Test
    void testUpdateCustomer_found() {
        Customer updatedDetails = Customer.builder()
                .fullName("Updated Name")
                .email("updated@example.com")
                .isActive(false)
                .build(); // ID will be set by service to match

        // Mock findById for the UpdateCustomerCommand
        when(customerRepository.findById(sampleCustomerId)).thenReturn(Optional.of(new Customer(
                sampleCustomer.getId(),
                sampleCustomer.getFullName(),
                sampleCustomer.getPhoneNumber(),
                sampleCustomer.getEmail(),
                sampleCustomer.getAddress(),
                sampleCustomer.isActive(),
                sampleCustomer.getCreatedAt(),
                sampleCustomer.getUpdatedAt()
        ))); // Return a copy to avoid direct modification issues

        // Mock save for the UpdateCustomerCommand
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer c = invocation.getArgument(0);
            c.setUpdatedAt(LocalDateTime.now()); // Simulate repo setting updatedAt
            return c;
        });

        Optional<Customer> resultOpt = customerService.updateCustomer(sampleCustomerId, updatedDetails);

        assertTrue(resultOpt.isPresent());
        Customer result = resultOpt.get();
        assertEquals(sampleCustomerId, result.getId());
        assertEquals("Updated Name", result.getFullName());
        assertEquals("updated@example.com", result.getEmail()); // Email is updated
//        assertFalse(result.isActive());
        assertTrue(result.isActive(), "Status 'active' seharusnya tetap true.");
        assertTrue(result.getUpdatedAt().isAfter(sampleCustomer.getUpdatedAt()));

        verify(customerRepository, times(1)).findById(sampleCustomerId); // Called by command
        verify(customerRepository, times(1)).save(any(Customer.class)); // Called by command
    }

    @Test
    void testUpdateCustomer_notFound() {
        UUID notFoundId = UUID.randomUUID();
        Customer updatedDetails = Customer.builder().fullName("Non Existent").build();

        when(customerRepository.findById(notFoundId)).thenReturn(Optional.empty()); // For command

        Optional<Customer> result = customerService.updateCustomer(notFoundId, updatedDetails);

        assertFalse(result.isPresent());
        verify(customerRepository, times(1)).findById(notFoundId); // Called by command
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void testDeleteCustomer_found() {
        // Mock findById for the DeleteCustomerCommand
        when(customerRepository.findById(sampleCustomerId)).thenReturn(Optional.of(sampleCustomer));
        // No need to mock deleteById for void, Mockito handles it. If it returned something, we'd mock that.

        boolean result = customerService.deleteCustomer(sampleCustomerId);

        assertTrue(result);
        verify(customerRepository, times(1)).findById(sampleCustomerId); // Called by command
        verify(customerRepository, times(1)).deleteById(sampleCustomerId); // Called by command
    }

    @Test
    void testDeleteCustomer_notFound() {
        UUID notFoundId = UUID.randomUUID();
        when(customerRepository.findById(notFoundId)).thenReturn(Optional.empty()); // For command

        boolean result = customerService.deleteCustomer(notFoundId);

        assertFalse(result);
        verify(customerRepository, times(1)).findById(notFoundId); // Called by command
        verify(customerRepository, never()).deleteById(notFoundId);
    }

    @Test
    void testSearchCustomers() {
        Customer customer1 = Customer.builder().fullName("Alice Smith").email("alice@mail.com").build();
        Customer customer2 = Customer.builder().fullName("Bob Johnson").email("bob@test.com").build();
        Customer customer3 = Customer.builder().fullName("Alice Brown").email("brown@test.com").build();
        List<Customer> allCustomers = Arrays.asList(customer1, customer2, customer3);

        when(customerRepository.findAll()).thenReturn(allCustomers);
        SearchStrategy fullNameStrategy = new SearchByFullNameStrategy();

        List<Customer> searchResults = customerService.searchCustomers("Alice", fullNameStrategy);

        assertEquals(2, searchResults.size());
        assertTrue(searchResults.stream().anyMatch(c -> c.getFullName().equals("Alice Smith")));
        assertTrue(searchResults.stream().anyMatch(c -> c.getFullName().equals("Alice Brown")));

        verify(customerRepository, times(1)).findAll();
    }
}