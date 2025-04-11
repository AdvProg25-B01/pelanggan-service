package id.ac.ui.cs.advprog.pelangganservice.command;

import id.ac.ui.cs.advprog.pelangganservice.repository.CustomerRepository;

public class DeleteCustomerCommand {

    private final CustomerRepository repository;

    public DeleteCustomerCommand(CustomerRepository repository) {
        this.repository = repository;
    }

    /**
     * Mengeksekusi perintah untuk menghapus customer berdasarkan ID.
     * @param customerId ID customer yang akan dihapus.
     * @throws IllegalArgumentException jika customer tidak ditemukan.
     */
    public void execute(String customerId) {
        // Coba panggil metode delete di repository, jika customer tidak ada,
        // repository akan melempar exception sesuai implementasinya.
        repository.delete(customerId);
    }
}
