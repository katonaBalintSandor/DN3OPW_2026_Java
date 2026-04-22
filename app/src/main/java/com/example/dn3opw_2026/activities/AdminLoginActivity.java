package com.example.dn3opw_2026.activities;

import android.content.Intent;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.dn3opw_2026.R;
import com.example.dn3opw_2026.network.responses.AdminLoginResponse;
import com.example.dn3opw_2026.repository.AdminRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminLoginActivity extends AppCompatActivity {

    private EditText usernameEntry;
    private EditText passwordEntry;
    private EditText adminCodeEntry;

    private Button loginButton;
    private Button backButton;
    private Button exitButton;

    private android.widget.ImageView bgImage;

    private AdminRepository adminRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        adminRepository = new AdminRepository();

        bgImage = findViewById(R.id.bgImage);
        usernameEntry = findViewById(R.id.usernameEntry);
        passwordEntry = findViewById(R.id.passwordEntry);
        adminCodeEntry = findViewById(R.id.adminCodeEntry);

        loginButton = findViewById(R.id.loginButton);
        backButton = findViewById(R.id.backButton);
        exitButton = findViewById(R.id.exitButton);

        setupBackgroundBlur();
        setupListeners();
        setupBackPressed();
    }

    private void setupBackgroundBlur() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            bgImage.setRenderEffect(
                    RenderEffect.createBlurEffect(20f, 20f, Shader.TileMode.CLAMP)
            );
        }
    }

    private void setupListeners() {
        loginButton.setOnClickListener(v -> loginAdmin());

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(AdminLoginActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        exitButton.setOnClickListener(v -> confirmExit());
    }

    private void loginAdmin() {
        String username = usernameEntry.getText().toString().trim();
        String password = passwordEntry.getText().toString().trim();
        String adminCode = adminCodeEntry.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty() || adminCode.isEmpty()) {
            showMessage("Hiányzó adatok", "Kérlek, töltsd ki az összes mezőt!");
            return;
        }

        adminRepository.loginAdmin(username, password, adminCode).enqueue(new Callback<AdminLoginResponse>() {
            @Override
            public void onResponse(Call<AdminLoginResponse> call, Response<AdminLoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AdminLoginResponse result = response.body();

                    if (result.isSuccess()) {
                        Intent intent = new Intent(AdminLoginActivity.this, AdminDashboardActivity.class);
                        intent.putExtra("admin_id", result.getAdmin_id());
                        intent.putExtra("username", result.getUsername());
                        intent.putExtra("email", result.getEmail());
                        intent.putExtra("firstname", result.getFirstname());
                        intent.putExtra("lastname", result.getLastname());
                        intent.putExtra("library_id", result.getLibrary_id());
                        startActivity(intent);
                        finish();
                    } else {
                        showMessage("Hiba", "Hibás bejelentkezési adatok!");
                    }
                } else {
                    showMessage("Hiba", "Szerverhiba történt.");
                }
            }

            @Override
            public void onFailure(Call<AdminLoginResponse> call, Throwable t) {
                showMessage("Hálózati hiba", "Nem sikerült csatlakozni a szerverhez.\n" + t.getMessage());
            }
        });
    }

    private void confirmExit() {
        new AlertDialog.Builder(this)
                .setTitle("Kilépés")
                .setMessage("Biztosan ki akarsz lépni?")
                .setPositiveButton("Igen", (dialog, which) -> finishAffinity())
                .setNegativeButton("Nem", null)
                .show();
    }

    private void showMessage(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private void setupBackPressed() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Intent intent = new Intent(AdminLoginActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}