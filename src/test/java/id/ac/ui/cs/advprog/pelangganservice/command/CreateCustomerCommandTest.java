package id.ac.ui.cs.advprog.pelangganservice.command;

import id.ac.ui.cs.advprog.pelangganservice.model.Customer;
import id.ac.ui.cs.advprog.pelangganservice.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CreateCustomerCommandTest {

    @Mock
    private CustomerRepository customerRepository;

    @Test
    void testExecute() {
        Customer customerToCreate = Customer.builder()
                .fullName("New Customer")
                .email("new@example.com")
                .build();

        // This is important: the command itself doesn't set ID/timestamps, the repo does
        // So we mock what the repo would return after setting those
        Customer customerAfterSave = Customer.builder()
                .id(java.util.UUID.randomUUID()) // Simulate ID generation
                .fullName("New Customer")
                .email("new@example.com")
                .createdAt(java.time.LocalDateTime.now()) // Simulate timestamp
                .updatedAt(java.time.LocalDateTime.now())
                .isActive(true)
                .build();

        when(customerRepository.save(customerToCreate)).thenReturn(customerAfterSave);

        CreateCustomerCommand command = new CreateCustomerCommand(customerRepository, customerToCreate);
        Customer result = command.execute();

        assertNotNull(result);
        assertEquals(customerAfterSave.getId(), result.getId());
        assertEquals("New Customer", result.getFullName());
        assertNotNull(result.getCreatedAt()); // Check that repo simulation worked
        assertTrue(result.isActive());

        verify(customerRepository, times(1)).save(customerToCreate);
    }
}