package com.example.dn3opw_2026.network.responses;

import com.example.dn3opw_2026.model.Book;
import java.util.List;

public class LeasedBooksResponse {

    private boolean success;
    private List<Book> books;

    public boolean isSuccess() {
        return success;
    }

    public List<Book> getBooks() {
        return books;
    }
}