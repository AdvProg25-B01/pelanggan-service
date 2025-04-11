package id.ac.ui.cs.advprog.pelangganservice.model;

public class Customer {

    private String id;
    private String name;
    private String email;

    public Customer(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    // Getter dan Setter yang dibutuhkan
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    // Tambahkan setter jika diperlukan
}
