package com.example.dn3opw_2026.network.responses;

import com.example.dn3opw_2026.model.Book;

public class BookDetailResponse {
    private boolean success;
    private Book book;
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public Book getBook() {
        return book;
    }

    public String getMessage() {
        return message;
    }
}