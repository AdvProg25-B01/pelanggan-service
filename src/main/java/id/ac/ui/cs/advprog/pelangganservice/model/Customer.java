package id.ac.ui.cs.advprog.pelangganservice.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Customer {
    private UUID id;
    private String fullName;
    private String phoneNumber;
    private String email;
    private String address;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructor, getter, setter, toString, equals, hashCode bisa ditambahkan jika diperlukan
}