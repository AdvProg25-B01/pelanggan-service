package id.ac.ui.cs.advprog.pelangganservice.dto;

import id.ac.ui.cs.advprog.pelangganservice.model.Customer;

public class CustomerMapper {

    public static CustomerResponseDTO toResponseDTO(Customer customer) {
        if (customer == null) {
            return null;
        }
        return CustomerResponseDTO.builder()
                .id(customer.getId())
                .fullName(customer.getFullName())
                .phoneNumber(customer.getPhoneNumber())
                .email(customer.getEmail())
                .address(customer.getAddress())
                .isActive(customer.isActive())
                .createdAt(customer.getCreatedAt())
                .updatedAt(customer.getUpdatedAt())
                .build();
    }

    public static Customer toEntity(CreateCustomerRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        return Customer.builder()
                .fullName(dto.getFullName())
                .phoneNumber(dto.getPhoneNumber())
                .email(dto.getEmail())
                .address(dto.getAddress())
                // id, isActive, createdAt, updatedAt akan di-set oleh service/JPA
                .build();
    }

    public static void updateEntityFromDTO(UpdateCustomerRequestDTO dto, Customer customerToUpdate) {
        if (dto == null || customerToUpdate == null) {
            return;
        }
        if (dto.getFullName() != null) {
            customerToUpdate.setFullName(dto.getFullName());
        }
        if (dto.getPhoneNumber() != null) {
            customerToUpdate.setPhoneNumber(dto.getPhoneNumber());
        }
        if (dto.getEmail() != null) {
            customerToUpdate.setEmail(dto.getEmail());
        }
        if (dto.getAddress() != null) {
            customerToUpdate.setAddress(dto.getAddress());
        }
        // Contoh jika Anda mengizinkan update isActive via DTO:
        // if (dto.getIsActive() != null) {
        //     customerToUpdate.setActive(dto.getIsActive());
        // }
    }
}