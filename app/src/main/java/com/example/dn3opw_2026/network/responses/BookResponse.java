package com.example.dn3opw_2026.network.responses;

import com.example.dn3opw_2026.model.Book;
import java.util.List;

public class BookResponse {
    private boolean success;
    private String message;
    private List<Book> books;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<Book> getBooks() {
        return books;
    }
}