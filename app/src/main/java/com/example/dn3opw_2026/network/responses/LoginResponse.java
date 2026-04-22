package com.example.dn3opw_2026.network.responses;

import com.example.dn3opw_2026.model.User;

public class LoginResponse {
    private boolean success;
    private String message;
    private User user;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public User getUser() {
        return user;
    }
}