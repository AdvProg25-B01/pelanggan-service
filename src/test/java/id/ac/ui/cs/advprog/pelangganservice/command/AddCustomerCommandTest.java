package id.ac.ui.cs.advprog.pelangganservice.command;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AddCustomerCommandTest {

    // Contoh property atau mock yang dibutuhkan
    // private CustomerRepository customerRepository;
    // private AddCustomerCommand addCustomerCommand;

    @BeforeEach
    void setUp() {
        // Inisialisasi dependency, bisa pakai mock (Mockito) kalau mau
        // customerRepository = mock(CustomerRepository.class);
        // addCustomerCommand = new AddCustomerCommand(customerRepository);
    }

    @Test
    void testAddCustomerSuccess() {
        // 1. Definisikan data customer baru
        // 2. Eksekusi command
        // 3. Pastikan berhasil (assert)

        fail("Not yet implemented (RED).");
    }

    @Test
    void testAddCustomerFailureWhenDataInvalid() {
        // 1. Siapkan data invalid (misalnya nama kosong)
        // 2. Eksekusi command
        // 3. Pastikan dilempar exception atau hasilnya error

        fail("Not yet implemented (RED).");
    }
}
