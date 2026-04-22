package com.example.dn3opw_2026.network.responses;

import com.example.dn3opw_2026.model.Book;

public class AdminBookDetailResponse {
    private boolean success;
    private String message;
    private Book book;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Book getBook() {
        return book;
    }
}