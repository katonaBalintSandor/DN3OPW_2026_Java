package com.example.dn3opw_2026.repository;

import com.example.dn3opw_2026.network.ApiClient;
import com.example.dn3opw_2026.network.ApiService;
import com.example.dn3opw_2026.network.responses.LibrariesResponse;
import com.example.dn3opw_2026.network.responses.LibraryDetailResponse;
import retrofit2.Call;

public class LibraryRepository {

    private final ApiService apiService;

    public LibraryRepository() {
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    public Call<LibrariesResponse> getLibraries() {
        return apiService.getLibraries();
    }

    public Call<LibraryDetailResponse> getLibraryDetails(int libraryId) {
        return apiService.getLibraryDetails(libraryId);
    }
}