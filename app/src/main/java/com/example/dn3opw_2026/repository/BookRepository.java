package com.example.dn3opw_2026.repository;

import com.example.dn3opw_2026.network.ApiClient;
import com.example.dn3opw_2026.network.ApiService;
import com.example.dn3opw_2026.network.responses.BaseResponse;
import com.example.dn3opw_2026.network.responses.BookDetailResponse;
import com.example.dn3opw_2026.network.responses.BookResponse;
import com.example.dn3opw_2026.network.responses.LeasedBooksResponse;
import retrofit2.Call;

public class BookRepository {

    private final ApiService apiService;

    public BookRepository() {
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    public Call<BookResponse> getAllBooks() {
        return apiService.getAllBooks();
    }

    public Call<BookDetailResponse> getBookDetails(int bookId, int libraryId) {
        return apiService.getBookDetails(bookId, libraryId);
    }

    public Call<BaseResponse> leaseBook(int userId, int libraryId, int bookId) {
        return apiService.leaseBook(userId, libraryId, bookId);
    }

    public Call<LeasedBooksResponse> getLeasedBooks(int userId) {
        return apiService.getLeasedBooks(userId);
    }

    public Call<BaseResponse> returnBook(int leaseId, int bookId, int libraryId) {
        return apiService.returnBook(leaseId, bookId, libraryId);
    }

    public Call<BaseResponse> updatePassword(int userId, String password) {
        return apiService.updatePassword(userId, password);
    }
}