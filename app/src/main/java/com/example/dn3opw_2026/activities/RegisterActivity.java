package com.example.dn3opw_2026.activities;

import android.content.Intent;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.os.Build;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.example.dn3opw_2026.R;
import com.example.dn3opw_2026.network.responses.RegisterResponse;
import com.example.dn3opw_2026.repository.AuthRepository;
import java.util.regex.Pattern;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText firstnameEntry;
    private EditText lastnameEntry;
    private EditText usernameEntry;
    private EditText emailEntry;
    private EditText passwordEntry;
    private EditText confirmEntry;

    private Button registerButton;
    private Button backButton;

    private ImageView bgImage;

    private AuthRepository authRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        authRepository = new AuthRepository();

        bgImage = findViewById(R.id.bgImage);

        firstnameEntry = findViewById(R.id.firstnameEntry);
        lastnameEntry = findViewById(R.id.lastnameEntry);
        usernameEntry = findViewById(R.id.usernameEntry);
        emailEntry = findViewById(R.id.emailEntry);
        passwordEntry = findViewById(R.id.passwordEntry);
        confirmEntry = findViewById(R.id.confirmEntry);

        registerButton = findViewById(R.id.registerButton);
        backButton = findViewById(R.id.backButton);

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
        registerButton.setOnClickListener(v -> register());

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private boolean isPasswordStrong(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasLower = Pattern.compile("[a-z]").matcher(password).find();
        boolean hasUpper = Pattern.compile("[A-Z]").matcher(password).find();
        boolean hasSpecial = Pattern.compile("[\\W_]").matcher(password).find();

        return hasLower && hasUpper && hasSpecial;
    }

    private void register() {
        String lastname = lastnameEntry.getText().toString().trim();
        String firstname = firstnameEntry.getText().toString().trim();
        String username = usernameEntry.getText().toString().trim();
        String email = emailEntry.getText().toString().trim();
        String password = passwordEntry.getText().toString().trim();
        String confirm = confirmEntry.getText().toString().trim();

        if (firstname.isEmpty() || lastname.isEmpty() || username.isEmpty()
                || email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            showMessage("Hiányzó adat", "Kérlek töltsd ki az összes mezőt.");
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showMessage("Érvénytelen", "Nem érvényes email cím!");
            return;
        }

        if (!isPasswordStrong(password)) {
            showMessage(
                    "Gyenge jelszó",
                    "A jelszónak legalább 8 karakter hosszúnak kell lennie, és tartalmaznia kell kis- és nagybetűt, valamint egy speciális karaktert."
            );
            return;
        }

        if (!password.equals(confirm)) {
            showMessage("Hiba", "A jelszavak nem egyeznek.");
            return;
        }

        authRepository.register(lastname, firstname, username, email, password)
                .enqueue(new Callback<RegisterResponse>() {
                    @Override
                    public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            RegisterResponse result = response.body();

                            if (result.isSuccess()) {
                                showSuccessAndGoBack();
                            } else {
                                showMessage("Hiba", result.getMessage());
                            }
                        } else {
                            showMessage("Hiba", "Szerverhiba történt.");
                        }
                    }

                    @Override
                    public void onFailure(Call<RegisterResponse> call, Throwable t) {
                        showMessage("Hálózati hiba", "Nem sikerült csatlakozni a szerverhez.\n" + t.getMessage());
                    }
                });
    }

    private void showSuccessAndGoBack() {
        new AlertDialog.Builder(this)
                .setTitle("Sikeres regisztráció")
                .setMessage("A regisztráció sikeres volt.")
                .setPositiveButton("OK", (dialog, which) -> {
                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                })
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
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}