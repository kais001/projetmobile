package tn.esprit.finalproject.Network;

public class UserResponse {
    private int id;       // JSON key for user ID
    private String email; // JSON key for user email
    private String name;  // JSON key for user name

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
