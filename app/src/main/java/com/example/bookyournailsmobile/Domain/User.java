package com.example.bookyournailsmobile.Domain;

public class User {
    private String user_id;
    private String full_name;
    private String email;
    private String first_name;
    private String last_name;
    private String password;
    private String role; // Add this line

    // Getters and Setters
    public String getId() {
        return user_id;
    }

    public void setId(String id) {
        this.user_id = id;
    }

    public String getFullname() {
        return full_name;
    }

    public void setFullname(String fullname) {
        this.full_name = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstname() {
        return first_name;
    }
    public String getLastname() {
        return last_name;
    }

    public void setFirstname(String firstname) {
        this.first_name = firstname;
    }
    public void setLastname(String lastname) {
        this.last_name = lastname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}