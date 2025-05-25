package id.ac.ui.cs.advprog.pelangganservice.service;

// Import DTOs
import id.ac.ui.cs.advprog.pelangganservice.dto.CustomerMapper;
import id.ac.ui.cs.advprog.pelangganservice.dto.UpdateCustomerRequestDTO;
import id.ac.ui.cs.advprog.pelangganservice.model.Customer;
import id.ac.ui.cs.advprog.pelangganservice.repository.CustomerRepository;
import id.ac.ui.cs.advprog.pelangganservice.strategy.SearchByFullNameStrategy;
import id.ac.ui.cs.advprog.pelangganservice.strategy.SearchStrategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito; // Untuk spy jika diperlukan
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

    private Customer sampleCustomerEntity; // Mengganti nama agar lebih jelas ini entitas
    private UUID sampleCustomerId;

    @BeforeEach
    void setUp() {
        sampleCustomerId = UUID.randomUUID();
        sampleCustomerEntity = Customer.builder()
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
        Customer newCustomerDetailsEntity = Customer.builder() // Ini adalah entitas yang dikirim ke service
                .fullName("New User")
                .email("newuser@example.com")
                .phoneNumber("9876543210")
                .address("456 New Ave")
                .build();

        // Mock repository.save (dipanggil dari dalam CreateCustomerCommand)
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer c = invocation.getArgument(0);
            // Service sudah set ID, isActive. JPA akan set createdAt, updatedAt.
            // Mock ini mensimulasikan JPA mengisi timestamp jika belum ada (meskipun tidak akan terjadi jika @CreationTimestamp aktif)
            if (c.getCreatedAt() == null) c.setCreatedAt(LocalDateTime.now());
            if (c.getUpdatedAt() == null) c.setUpdatedAt(LocalDateTime.now());
            return c;
        });

        Customer createdCustomer = customerService.createCustomer(newCustomerDetailsEntity);

        assertNotNull(createdCustomer.getId());
        assertEquals("New User", createdCustomer.getFullName());
        assertTrue(createdCustomer.isActive()); // Di-set oleh service
        assertNotNull(createdCustomer.getCreatedAt()); // Di-set oleh JPA/@CreationTimestamp (disimulasikan mock)
        assertNotNull(createdCustomer.getUpdatedAt()); // Di-set oleh JPA/@UpdateTimestamp (disimulasikan mock)

        verify(customerRepository, times(1)).save(argThat(savedCustomer ->
                savedCustomer.getId() != null &&
                        savedCustomer.getFullName().equals("New User") &&
                        savedCustomer.isActive() // isActive di-set oleh service
        ));
    }

    @Test
    void testCreateCustomer_setsTimestampsAndActive() {
        Customer customerToCreateEntity = Customer.builder() // Ini entitas yang dikirim ke service
                .fullName("Fresh Customer")
                .email("fresh@example.com")
                .build();

        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer c = invocation.getArgument(0);
            if (c.getId() == null) c.setId(UUID.randomUUID()); // Service sudah set ID
            c.setActive(true); // Service sudah set isActive
            // Simulasikan JPA mengisi timestamp
            c.setCreatedAt(LocalDateTime.now());
            c.setUpdatedAt(LocalDateTime.now());
            return c;
        });

        Customer result = customerService.createCustomer(customerToCreateEntity);

        assertNotNull(result.getId());
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
        assertTrue(result.isActive());

        verify(customerRepository).save(argThat(savedCustomer ->
                savedCustomer.getId() != null &&
                        savedCustomer.getFullName().equals("Fresh Customer") &&
                        savedCustomer.isActive()
        ));
    }


    @Test
    void testGetAllCustomers() {
        List<Customer> customers = Arrays.asList(sampleCustomerEntity, Customer.builder().id(UUID.randomUUID()).build());
        when(customerRepository.findAll()).thenReturn(customers);

        List<Customer> result = customerService.getAllCustomers();

        assertEquals(2, result.size());
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void testGetCustomerById_found() {
        when(customerRepository.findById(sampleCustomerId)).thenReturn(Optional.of(sampleCustomerEntity));

        Optional<Customer> result = customerService.getCustomerById(sampleCustomerId);

        assertTrue(result.isPresent());
        assertEquals(sampleCustomerEntity, result.get());
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
        // DTO yang dikirim ke service.updateCustomer
        UpdateCustomerRequestDTO updateRequestDTO = UpdateCustomerRequestDTO.builder()
                .fullName("Updated Name From DTO")
                .email("updated.dto@example.com")
                // phoneNumber dan address null, tidak akan diupdate oleh CustomerMapper.updateEntityFromDTO
                .build();

        // Salinan dari sampleCustomerEntity yang akan dimodifikasi
        // Penting untuk membuat salinan baru agar objek asli sampleCustomerEntity tidak berubah
        // dan bisa digunakan untuk perbandingan state awal.
        Customer existingCustomerInDb = Customer.builder()
                .id(sampleCustomerEntity.getId())
                .fullName(sampleCustomerEntity.getFullName())
                .email(sampleCustomerEntity.getEmail())
                .phoneNumber(sampleCustomerEntity.getPhoneNumber())
                .address(sampleCustomerEntity.getAddress())
                .isActive(sampleCustomerEntity.isActive())
                .createdAt(sampleCustomerEntity.getCreatedAt())
                .updatedAt(sampleCustomerEntity.getUpdatedAt())
                .build();

        // Mock findById untuk mengembalikan salinan entitas yang ada
        when(customerRepository.findById(sampleCustomerId)).thenReturn(Optional.of(existingCustomerInDb));

        // Mock save untuk mengembalikan entitas yang sudah dimodifikasi
        // CustomerMapper.updateEntityFromDTO akan dipanggil di dalam service,
        // jadi kita hanya perlu memastikan save mengembalikan entitas yang sudah termodifikasi tersebut.
        // Dan JPA/@UpdateTimestamp akan mengurus updatedAt.
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer customerToSave = invocation.getArgument(0);
            // Simulasikan @UpdateTimestamp
            customerToSave.setUpdatedAt(LocalDateTime.now().plusMinutes(1)); // Beri nilai berbeda untuk pengecekan
            return customerToSave;
        });

        // Panggil metode service dengan DTO
        Optional<Customer> resultOpt = customerService.updateCustomer(sampleCustomerId, updateRequestDTO);

        assertTrue(resultOpt.isPresent());
        Customer result = resultOpt.get();

        assertEquals(sampleCustomerId, result.getId());
        assertEquals("Updated Name From DTO", result.getFullName()); // Diupdate dari DTO
        assertEquals("updated.dto@example.com", result.getEmail()); // Diupdate dari DTO
        assertEquals(sampleCustomerEntity.getPhoneNumber(), result.getPhoneNumber()); // Tidak diupdate, tetap dari original
        assertEquals(sampleCustomerEntity.getAddress(), result.getAddress());         // Tidak diupdate, tetap dari original
        assertTrue(result.isActive()); // Asumsi isActive tidak diubah jika tidak ada di DTO
        assertEquals(sampleCustomerEntity.getCreatedAt(), result.getCreatedAt()); // CreatedAt tidak berubah
        assertTrue(result.getUpdatedAt().isAfter(sampleCustomerEntity.getUpdatedAt()), "UpdatedAt should be newer");

        // Verifikasi findById dipanggil
        verify(customerRepository, times(1)).findById(sampleCustomerId);
        // Verifikasi save dipanggil dengan entitas yang field-nya sudah diupdate sesuai DTO
        verify(customerRepository, times(1)).save(argThat(savedCustomer ->
                savedCustomer.getId().equals(sampleCustomerId) &&
                        savedCustomer.getFullName().equals("Updated Name From DTO") &&
                        savedCustomer.getEmail().equals("updated.dto@example.com") &&
                        // Pastikan field yang tidak ada di DTO tidak berubah (jika itu perilakunya)
                        savedCustomer.getPhoneNumber().equals(sampleCustomerEntity.getPhoneNumber()) &&
                        savedCustomer.getAddress().equals(sampleCustomerEntity.getAddress()) &&
                        savedCustomer.isActive() == sampleCustomerEntity.isActive() // isActive tetap
        ));
    }

    @Test
    void testUpdateCustomer_notFound() {
        UUID notFoundId = UUID.randomUUID();
        UpdateCustomerRequestDTO updateRequestDTO = UpdateCustomerRequestDTO.builder()
                .fullName("Non Existent DTO").build();

        when(customerRepository.findById(notFoundId)).thenReturn(Optional.empty());

        Optional<Customer> result = customerService.updateCustomer(notFoundId, updateRequestDTO);

        assertFalse(result.isPresent());
        verify(customerRepository, times(1)).findById(notFoundId);
        verify(customerRepository, never()).save(any(Customer.class));
    }

    @Test
    void testDeleteCustomer_found() {
        when(customerRepository.findById(sampleCustomerId)).thenReturn(Optional.of(sampleCustomerEntity));
        // Mockito akan menangani void method seperti deleteById tanpa perlu when().then...
        // kecuali Anda ingin memverifikasi sesuatu yang sangat spesifik terjadi saat dipanggil.

        boolean result = customerService.deleteCustomer(sampleCustomerId);

        assertTrue(result);
        verify(customerRepository, times(1)).findById(sampleCustomerId); // Dipanggil oleh DeleteCustomerCommand
        verify(customerRepository, times(1)).deleteById(sampleCustomerId); // Dipanggil oleh DeleteCustomerCommand
    }

    @Test
    void testDeleteCustomer_notFound() {
        UUID notFoundId = UUID.randomUUID();
        when(customerRepository.findById(notFoundId)).thenReturn(Optional.empty());

        boolean result = customerService.deleteCustomer(notFoundId);

        assertFalse(result);
        verify(customerRepository, times(1)).findById(notFoundId);
        verify(customerRepository, never()).deleteById(notFoundId);
    }

    @Test
    void testSearchCustomers() {
        Customer customer1 = Customer.builder().fullName("Alice Smith").email("alice@mail.com").build();
        Customer customer2 = Customer.builder().fullName("Bob Johnson").email("bob@test.com").build();
        Customer customer3 = Customer.builder().fullName("Alice Brown").email("brown@test.com").build();
        List<Customer> allCustomers = Arrays.asList(customer1, customer2, customer3);

        when(customerRepository.findAll()).thenReturn(allCustomers);
        SearchStrategy fullNameStrategy = new SearchByFullNameStrategy(); // Atau mock strategy jika diperlukan

        List<Customer> searchResults = customerService.searchCustomers("Alice", fullNameStrategy);

        assertEquals(2, searchResults.size());
        assertTrue(searchResults.stream().anyMatch(c -> c.getFullName().equals("Alice Smith")));
        assertTrue(searchResults.stream().anyMatch(c -> c.getFullName().equals("Alice Brown")));

        verify(customerRepository, times(1)).findAll();
    }
}