package com.example.dn3opw_2026.activities;

import android.content.Intent;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.dn3opw_2026.R;
import com.example.dn3opw_2026.model.User;
import com.example.dn3opw_2026.network.responses.LoginResponse;
import com.example.dn3opw_2026.repository.AuthRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText usernameEntry;
    private EditText passwordEntry;
    private Button loginButton;
    private Button adminLoginButton;
    private Button registerButton;
    private Button exitButton;
    private ImageView bgImage;

    private AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authRepository = new AuthRepository();

        bgImage = findViewById(R.id.bgImage);
        usernameEntry = findViewById(R.id.usernameEntry);
        passwordEntry = findViewById(R.id.passwordEntry);
        loginButton = findViewById(R.id.loginButton);
        adminLoginButton = findViewById(R.id.adminLoginButton);
        registerButton = findViewById(R.id.registerButton);
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
        loginButton.setOnClickListener(v -> login());

        adminLoginButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, AdminLoginActivity.class);
            startActivity(intent);
        });

        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        exitButton.setOnClickListener(v -> confirmExit());
    }

    private void login() {
        String username = usernameEntry.getText().toString().trim();
        String password = passwordEntry.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty()) {
            showMessage("Hiányzó adatok", "Kérlek, töltsd ki az összes mezőt!");
            return;
        }

        authRepository.login(username, password).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse result = response.body();

                    if (result.isSuccess() && result.getUser() != null) {
                        User user = result.getUser();

                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                        intent.putExtra("user", user);
                        startActivity(intent);
                        finish();
                    } else {
                        showMessage("Hiba", result.getMessage() != null ? result.getMessage() : "Sikertelen bejelentkezés.");
                    }
                } else {
                    showMessage("Hiba", "Szerverhiba történt.");
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
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
                confirmExit();
            }
        });
    }
}