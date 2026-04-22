package com.example.dn3opw_2026.repository;

import com.example.dn3opw_2026.network.ApiClient;
import com.example.dn3opw_2026.network.ApiService;
import com.example.dn3opw_2026.network.responses.AdminBookDetailResponse;
import com.example.dn3opw_2026.network.responses.AdminCommunityTopicsResponse;
import com.example.dn3opw_2026.network.responses.AdminEventDetailResponse;
import com.example.dn3opw_2026.network.responses.AdminEventsResponse;
import com.example.dn3opw_2026.network.responses.AdminLeasesResponse;
import com.example.dn3opw_2026.network.responses.AdminLoginResponse;
import com.example.dn3opw_2026.network.responses.AdminTopicDetailsResponse;
import com.example.dn3opw_2026.network.responses.BaseResponse;
import com.example.dn3opw_2026.network.responses.BookResponse;
import com.example.dn3opw_2026.network.responses.LibraryDetailResponse;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;

public class AdminRepository {

    private final ApiService apiService;

    public AdminRepository() {
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    public Call<AdminLoginResponse> loginAdmin(String username, String password, String adminCode) {
        return apiService.loginAdmin(username, password, adminCode);
    }

    public Call<BookResponse> getAdminBooks(int libraryId) {
        return apiService.getAdminBooks(libraryId);
    }

    public Call<LibraryDetailResponse> getAdminLibrary(int libraryId) {
        return apiService.getAdminLibrary(libraryId);
    }

    public Call<BaseResponse> updateBookQuantity(int libraryId, int bookId, int quantity) {
        return apiService.updateBookQuantity(libraryId, bookId, quantity);
    }

    public Call<AdminBookDetailResponse> getAdminBookById(int bookId, int libraryId) {
        return apiService.getAdminBookById(bookId, libraryId);
    }

    public Call<BaseResponse> addAdminBook(
            RequestBody title,
            RequestBody author,
            RequestBody category,
            RequestBody releaseDate,
            RequestBody description,
            RequestBody uploadedBy,
            RequestBody libraryId,
            RequestBody quantity,
            MultipartBody.Part image
    ) {
        return apiService.addAdminBook(
                title,
                author,
                category,
                releaseDate,
                description,
                uploadedBy,
                libraryId,
                quantity,
                image
        );
    }

    public Call<BaseResponse> updateAdminBook(
            RequestBody bookId,
            RequestBody title,
            RequestBody author,
            RequestBody category,
            RequestBody releaseDate,
            RequestBody description,
            RequestBody oldPicture,
            MultipartBody.Part image
    ) {
        return apiService.updateAdminBook(
                bookId,
                title,
                author,
                category,
                releaseDate,
                description,
                oldPicture,
                image
        );
    }

    public Call<AdminLeasesResponse> getAdminLeases(int libraryId) {
        return apiService.getAdminLeases(libraryId);
    }

    public Call<BaseResponse> returnBook(int leaseId, int bookId, int libraryId) {
        return apiService.returnBook(leaseId, bookId, libraryId);
    }

    public Call<AdminCommunityTopicsResponse> getAdminCommunityTopics() {
        return apiService.getAdminCommunityTopics();
    }

    public Call<BaseResponse> deleteAdminTopic(int topicId) {
        return apiService.deleteAdminTopic(topicId);
    }

    public Call<BaseResponse> deleteAdminComment(int commentId) {
        return apiService.deleteAdminComment(commentId);
    }

    public Call<AdminTopicDetailsResponse> getAdminTopicDetails(int topicId) {
        return apiService.getAdminTopicDetails(topicId);
    }

    public Call<AdminEventsResponse> getAdminEvents() {
        return apiService.getAdminEvents();
    }

    public Call<AdminEventDetailResponse> getAdminEventById(int eventId) {
        return apiService.getAdminEventById(eventId);
    }

    public Call<BaseResponse> deleteEvent(int eventId) {
        return apiService.deleteEvent(eventId);
    }

    public Call<BaseResponse> updateAdminEvent(
            RequestBody eventId,
            RequestBody title,
            RequestBody header,
            RequestBody date,
            RequestBody description,
            RequestBody oldPicture,
            MultipartBody.Part image
    ) {
        return apiService.updateAdminEvent(
                eventId,
                title,
                header,
                date,
                description,
                oldPicture,
                image
        );
    }

    public Call<BaseResponse> addAdminEvent(
            RequestBody title,
            RequestBody header,
            RequestBody date,
            RequestBody description,
            RequestBody adminId,
            RequestBody libraryId,
            MultipartBody.Part image
    ) {
        return apiService.addAdminEvent(
                title,
                header,
                date,
                description,
                adminId,
                libraryId,
                image
        );
    }
}