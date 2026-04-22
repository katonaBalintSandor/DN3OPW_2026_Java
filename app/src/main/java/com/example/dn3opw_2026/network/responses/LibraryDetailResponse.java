package com.example.dn3opw_2026.network.responses;

import com.example.dn3opw_2026.model.Book;
import com.example.dn3opw_2026.model.Library;
import java.util.List;

public class LibraryDetailResponse {
    private boolean success;
    private String message;
    private Library library;
    private List<Book> books;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Library getLibrary() {
        return library;
    }

    public List<Book> getBooks() {
        return books;
    }
}