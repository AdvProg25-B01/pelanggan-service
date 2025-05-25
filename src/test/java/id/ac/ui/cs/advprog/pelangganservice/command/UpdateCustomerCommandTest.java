package id.ac.ui.cs.advprog.pelangganservice.command;

import id.ac.ui.cs.advprog.pelangganservice.model.Customer;
import id.ac.ui.cs.advprog.pelangganservice.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UpdateCustomerCommandTest {

    @Mock
    private CustomerRepository customerRepository;

    @Test
    void testExecute_customerExists() {
        UUID customerId = UUID.randomUUID();
        LocalDateTime initialUpdatedAt = LocalDateTime.now().minusHours(1);
        Customer existingCustomer = Customer.builder()
                .id(customerId)
                .fullName("Old Name")
                .email("old@example.com")
                .phoneNumber("123") // Tambahkan field yang diupdate oleh command
                .address("Old Address") // Tambahkan field yang diupdate oleh command
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(initialUpdatedAt)
                .isActive(true)
                .build();

        Customer updatedDetails = Customer.builder()
                .id(customerId) // ID must match
                .fullName("New Name")
                .email("new@example.com")
                .phoneNumber("456") // Tambahkan field yang diupdate oleh command
                .address("New Address") // Tambahkan field yang diupdate oleh command
                .isActive(false)
                .build();

        // Mocking findById
        when(customerRepository.findById(customerId)).thenReturn(Optional.of(existingCustomer));

        // Mocking save. The command will modify `existingCustomer` and then pass it to `save`
        // We expect the save method to be called with the modified `existingCustomer`
        // And it should return the successfully saved customer (which is the modified one)
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer customerToSave = invocation.getArgument(0);
            customerToSave.setUpdatedAt(LocalDateTime.now()); // Simulate repository setting updatedAt
            return customerToSave;
        });

        UpdateCustomerCommand command = new UpdateCustomerCommand(customerRepository, updatedDetails);
        Optional<Customer> resultOpt = command.execute();

        assertTrue(resultOpt.isPresent());
        Customer result = resultOpt.get();

        assertEquals(customerId, result.getId());
        assertEquals("New Name", result.getFullName());
        assertEquals("new@example.com", result.getEmail());
        assertEquals("456", result.getPhoneNumber());
        assertEquals("New Address", result.getAddress());
        assertTrue(result.isActive(), "Status 'active' seharusnya tetap true.");
        assertNotNull(result.getUpdatedAt());
        assertTrue(result.getUpdatedAt().isAfter(initialUpdatedAt), "UpdatedAt should be newer.");

        verify(customerRepository, times(1)).findById(customerId);
        verify(customerRepository, times(1)).save(argThat(savedCustomer ->
                savedCustomer.getId().equals(customerId) &&
                        savedCustomer.getFullName().equals("New Name") &&
                        savedCustomer.getEmail().equals("new@example.com") &&
                        savedCustomer.getPhoneNumber().equals("456") && // Pastikan ini juga dicek
                        savedCustomer.getAddress().equals("New Address") && // Pastikan ini juga dicek
                        savedCustomer.isActive() // Harus true
        ));
    }

    @Test
    void testExecute_customerNotExists() {
        UUID customerId = UUID.randomUUID();
        Customer updatedDetails = Customer.builder().id(customerId).fullName("New Name").build();

        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        UpdateCustomerCommand command = new UpdateCustomerCommand(customerRepository, updatedDetails);
        Optional<Customer> resultOpt = command.execute();

        assertFalse(resultOpt.isPresent());
        verify(customerRepository, times(1)).findById(customerId);
        verify(customerRepository, never()).save(any(Customer.class));
    }
}