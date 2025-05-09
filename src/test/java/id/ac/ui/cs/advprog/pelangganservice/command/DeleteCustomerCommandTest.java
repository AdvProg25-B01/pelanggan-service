package id.ac.ui.cs.advprog.pelangganservice.command;

import id.ac.ui.cs.advprog.pelangganservice.model.Customer;
import id.ac.ui.cs.advprog.pelangganservice.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import java.util.UUID;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DeleteCustomerCommandTest {

    @Mock
    private CustomerRepository customerRepository;

    @Test
    void testExecute_customerExists() {
        UUID customerId = UUID.randomUUID();
        Customer existingCustomer = Customer.builder().id(customerId).build();

        when(customerRepository.findById(customerId)).thenReturn(Optional.of(existingCustomer));
        // doNothing().when(customerRepository).deleteById(customerId); // For void methods

        DeleteCustomerCommand command = new DeleteCustomerCommand(customerRepository, customerId);
        boolean result = command.execute();

        assertTrue(result);
        verify(customerRepository, times(1)).findById(customerId);
        verify(customerRepository, times(1)).deleteById(customerId);
    }

    @Test
    void testExecute_customerNotExists() {
        UUID customerId = UUID.randomUUID();
        when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        DeleteCustomerCommand command = new DeleteCustomerCommand(customerRepository, customerId);
        boolean result = command.execute();

        assertFalse(result);
        verify(customerRepository, times(1)).findById(customerId);
        verify(customerRepository, never()).deleteById(customerId);
    }
}