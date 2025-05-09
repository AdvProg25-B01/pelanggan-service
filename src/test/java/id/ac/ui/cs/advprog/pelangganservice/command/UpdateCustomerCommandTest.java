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
        Customer existingCustomer = Customer.builder()
                .id(customerId)
                .fullName("Old Name")
                .email("old@example.com")
                .createdAt(LocalDateTime.now().minusDays(1))
                .updatedAt(LocalDateTime.now().minusHours(1))
                .isActive(true)
                .build();

        Customer updatedDetails = Customer.builder()
                .id(customerId) // ID must match
                .fullName("New Name")
                .email("new@example.com")
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
        assertFalse(result.isActive());
        assertNotNull(result.getUpdatedAt());
        assertTrue(result.getUpdatedAt().isAfter(existingCustomer.getUpdatedAt())); // Check if updatedAt is new

        verify(customerRepository, times(1)).findById(customerId);
        verify(customerRepository, times(1)).save(any(Customer.class)); // Check that save was called
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