package id.ac.ui.cs.advprog.pelangganservice.strategy;

import id.ac.ui.cs.advprog.pelangganservice.model.Customer;
import java.util.Collections; // Import Collections
import java.util.List;
import java.util.stream.Collectors;

public class SearchByPhoneNumberStrategy implements SearchStrategy {

    private String normalizePhoneNumber(String phoneNumber) {
        if (phoneNumber == null) {
            return "";
        }
        String numericOnly = phoneNumber.replaceAll("[^0-9]", "");
        return numericOnly;
    }


    @Override
    public List<Customer> search(List<Customer> customers, String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            // Mengembalikan list kosong lebih masuk akal untuk search jika tidak ada term
            return Collections.emptyList();
        }

        String normalizedSearchTerm = searchTerm.replaceAll("[^0-9]", "");
        if (normalizedSearchTerm.isEmpty()) {
            return Collections.emptyList(); // Jika setelah normalisasi jadi kosong
        }


        return customers.stream()
                .filter(customer -> {
                    String customerPhoneNumber = customer.getPhoneNumber();
                    if (customerPhoneNumber == null || customerPhoneNumber.trim().isEmpty()) {
                        return false;
                    }
                    String normalizedCustomerPhone = customerPhoneNumber.replaceAll("[^0-9]", "");

                    // Logika pencocokan yang lebih fleksibel:
                    // 1. Cek apakah nomor pelanggan yang dinormalisasi mengandung searchTerm yang dinormalisasi
                    if (normalizedCustomerPhone.contains(normalizedSearchTerm)) {
                        return true;
                    }
                    // 2. Kasus khusus: searchTerm adalah "08..." dan nomor pelanggan adalah "628..."
                    //    atau sebaliknya.
                    if (normalizedSearchTerm.startsWith("0") && normalizedCustomerPhone.startsWith("62")) {
                        // Bandingkan searchTerm (tanpa '0') dengan nomor pelanggan (tanpa '62')
                        if (normalizedCustomerPhone.substring(2).contains(normalizedSearchTerm.substring(1))) {
                            return true;
                        }
                    }
                    // Kasus khusus: searchTerm adalah "628..." dan nomor pelanggan adalah "08..."
                    if (normalizedSearchTerm.startsWith("62") && normalizedCustomerPhone.startsWith("0")) {
                        // Bandingkan searchTerm (tanpa '62') dengan nomor pelanggan (tanpa '0')
                        if (normalizedSearchTerm.substring(2).contains(normalizedCustomerPhone.substring(1))) {
                            return true;
                        }
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }
}