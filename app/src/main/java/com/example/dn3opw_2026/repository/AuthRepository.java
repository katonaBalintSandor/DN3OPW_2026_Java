package com.example.dn3opw_2026.repository;

import com.example.dn3opw_2026.network.ApiClient;
import com.example.dn3opw_2026.network.ApiService;
import com.example.dn3opw_2026.network.responses.LoginResponse;
import com.example.dn3opw_2026.network.responses.RegisterResponse;
import retrofit2.Call;

public class AuthRepository {

    private final ApiService apiService;

    public AuthRepository() {
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    public Call<LoginResponse> login(String username, String password) {
        return apiService.login(username, password);
    }

    public Call<RegisterResponse> register(
            String lastname,
            String firstname,
            String username,
            String email,
            String password
    ) {
        return apiService.register(lastname, firstname, username, email, password);
    }
}