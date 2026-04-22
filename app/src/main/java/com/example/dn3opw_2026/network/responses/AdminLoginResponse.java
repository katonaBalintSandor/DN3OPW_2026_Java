package com.example.dn3opw_2026.network.responses;

public class AdminLoginResponse {
    private boolean success;
    private String message;
    private int admin_id;
    private String username;
    private String email;
    private String firstname;
    private String lastname;
    private int library_id;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public int getAdmin_id() {
        return admin_id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public int getLibrary_id() {
        return library_id;
    }
}