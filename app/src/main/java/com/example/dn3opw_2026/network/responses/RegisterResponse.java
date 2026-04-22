package com.example.dn3opw_2026.network.responses;

public class RegisterResponse {
    private boolean success;
    private String message;
    private int user_id;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public int getUser_id() {
        return user_id;
    }
}