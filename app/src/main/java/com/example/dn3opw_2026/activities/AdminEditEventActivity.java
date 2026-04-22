package com.example.dn3opw_2026.activities;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.bumptech.glide.Glide;
import com.example.dn3opw_2026.R;
import com.example.dn3opw_2026.model.Event;
import com.example.dn3opw_2026.network.responses.BaseResponse;
import com.example.dn3opw_2026.repository.AdminRepository;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminEditEventActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageButton menuButton;
    private ImageView bgImage;

    private TextView headerTitle;
    private EditText titleEntry;
    private EditText headerEntry;
    private EditText dateEntry;
    private EditText descriptionEntry;

    private ImageView imagePreview;
    private Button selectImageButton;
    private Button saveImageButton;
    private TextView imageLabel;

    private Button saveChangesButton;
    private Button backButton;

    private Button profileButton;
    private Button communityButton;
    private Button eventsButton;
    private Button logoutButton;

    private int adminId;
    private int libraryId;
    private String firstname;
    private String lastname;
    private String username;
    private String email;

    private Event event;
    private Uri selectedImageUri;
    private boolean imageSaved = false;

    private AdminRepository adminRepository;

    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    imageSaved = false;

                    String fileName = getFileName(uri);
                    imageLabel.setText(fileName == null ? "Új kép kiválasztva" : fileName);

                    saveImageButton.setEnabled(true);
                    saveImageButton.setText("Mentés könyvtárba");

                    Glide.with(this)
                            .load(uri)
                            .placeholder(R.drawable.event_placeholder)
                            .error(R.drawable.event_placeholder)
                            .into(imagePreview);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_event);

        adminRepository = new AdminRepository();

        readIntentData();
        if (event == null) {
            finish();
            return;
        }

        initViews();
        setupBackgroundBlur();
        fillFields();
        setupListeners();
        setupBackPressed();
    }

    private void readIntentData() {
        Intent intent = getIntent();
        adminId = intent.getIntExtra("admin_id", -1);
        libraryId = intent.getIntExtra("library_id", -1);
        firstname = intent.getStringExtra("firstname");
        lastname = intent.getStringExtra("lastname");
        username = intent.getStringExtra("username");
        email = intent.getStringExtra("email");
        event = (Event) intent.getSerializableExtra("event");

        if (firstname == null) firstname = "";
        if (lastname == null) lastname = "";
        if (username == null) username = "";
        if (email == null) email = "";
    }

    private Intent createAdminIntent(Class<?> target) {
        Intent intent = new Intent(AdminEditEventActivity.this, target);
        intent.putExtra("admin_id", adminId);
        intent.putExtra("library_id", libraryId);
        intent.putExtra("firstname", firstname);
        intent.putExtra("lastname", lastname);
        intent.putExtra("username", username);
        intent.putExtra("email", email);
        return intent;
    }

    private void openAdminPage(Class<?> target) {
        startActivity(createAdminIntent(target));
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        menuButton = findViewById(R.id.menuButton);
        bgImage = findViewById(R.id.bgImage);

        headerTitle = findViewById(R.id.headerTitle);
        titleEntry = findViewById(R.id.titleEntry);
        headerEntry = findViewById(R.id.headerEntry);
        dateEntry = findViewById(R.id.dateEntry);
        descriptionEntry = findViewById(R.id.descriptionEntry);

        imagePreview = findViewById(R.id.imagePreview);
        selectImageButton = findViewById(R.id.selectImageButton);
        saveImageButton = findViewById(R.id.saveImageButton);
        imageLabel = findViewById(R.id.imageLabel);

        saveChangesButton = findViewById(R.id.saveChangesButton);
        backButton = findViewById(R.id.backButton);

        profileButton = findViewById(R.id.profileButton);
        communityButton = findViewById(R.id.communityButton);
        eventsButton = findViewById(R.id.eventsButton);
        logoutButton = findViewById(R.id.logoutButton);
    }

    private void setupBackgroundBlur() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            bgImage.setRenderEffect(
                    RenderEffect.createBlurEffect(12f, 12f, Shader.TileMode.CLAMP)
            );
        }
    }

    private void fillFields() {
        headerTitle.setText("Esemény szerkesztése");

        titleEntry.setText(safe(event.getTitle()));
        headerEntry.setText(safe(event.getHeader()));
        dateEntry.setText(safe(event.getDate()));
        descriptionEntry.setText(safe(event.getDescription()));

        String currentPicture = safe(event.getPicture());
        imageLabel.setText(currentPicture.isEmpty() ? "Nincs kiválasztva új kép" : currentPicture);

        String imageUrl = "http://10.0.2.2/szakdolgozat_api/assets/images/events/" +
                Uri.encode(currentPicture);

        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.event_placeholder)
                .error(R.drawable.event_placeholder)
                .into(imagePreview);

        saveImageButton.setEnabled(false);
        saveImageButton.setText("Mentés könyvtárba");
    }

    private void setupListeners() {
        menuButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        selectImageButton.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

        saveImageButton.setOnClickListener(v -> {
            if (selectedImageUri == null) {
                showMessage("Nincs kép", "Először válassz ki egy képet!");
                return;
            }

            imageSaved = true;
            saveImageButton.setEnabled(false);
            saveImageButton.setText("Mentve");
            showMessage("Siker", "A kép mentésre kész. A szerver elmenti a megfelelő mappákba.");
        });

        saveChangesButton.setOnClickListener(v -> saveChanges());

        backButton.setOnClickListener(v -> goBack());

        profileButton.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            openAdminPage(AdminProfileActivity.class);
        });

        communityButton.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            openAdminPage(AdminCommunityActivity.class);
        });

        eventsButton.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            openAdminPage(AdminEventActivity.class);
        });

        logoutButton.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            Intent intent = new Intent(AdminEditEventActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void saveChanges() {
        String title = titleEntry.getText().toString().trim();
        String header = headerEntry.getText().toString().trim();
        String date = dateEntry.getText().toString().trim();
        String description = descriptionEntry.getText().toString().trim();

        if (title.isEmpty() || header.isEmpty() || date.isEmpty() || description.isEmpty()) {
            showMessage("Hiányzó adatok", "Kérlek töltsd ki az összes mezőt!");
            return;
        }

        if (!isValidDate(date)) {
            showMessage("Hibás dátumformátum", "A dátum formátuma hibás! Használd ezt: ÉÉÉÉ-HH-NN");
            return;
        }

        if (selectedImageUri != null && !imageSaved) {
            showMessage("Kép nincs elmentve", "Előbb mentsd el az új képet a könyvtárba!");
            return;
        }

        try {
            MultipartBody.Part imagePart = null;

            if (selectedImageUri != null) {
                File imageFile = createTempFileFromUri(selectedImageUri);

                String mimeType = getContentResolver().getType(selectedImageUri);
                if (mimeType == null || mimeType.trim().isEmpty()) {
                    mimeType = "image/*";
                }

                RequestBody imageRequestBody = RequestBody.create(
                        MediaType.parse(mimeType),
                        imageFile
                );

                imagePart = MultipartBody.Part.createFormData(
                        "image",
                        imageFile.getName(),
                        imageRequestBody
                );
            }

            adminRepository.updateAdminEvent(
                    createPart(String.valueOf(event.getId())),
                    createPart(title),
                    createPart(header),
                    createPart(date),
                    createPart(description),
                    createPart(safe(event.getPicture())),
                    imagePart
            ).enqueue(new Callback<BaseResponse>() {
                @Override
                public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        Toast.makeText(AdminEditEventActivity.this, "Esemény módosítva!", Toast.LENGTH_SHORT).show();

                        Intent intent = createAdminIntent(AdminEventDetailsActivity.class);
                        intent.putExtra("event_id", event.getId());
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(intent);
                        finish();
                    } else {
                        String message = "Nem sikerült módosítani az eseményt.";
                        if (response.body() != null && response.body().getMessage() != null) {
                            message = response.body().getMessage();
                        }
                        showMessage("Hiba", message);
                    }
                }

                @Override
                public void onFailure(Call<BaseResponse> call, Throwable t) {
                    showMessage("Hiba", "Hálózati hiba: " + t.getMessage());
                }
            });

        } catch (Exception e) {
            showMessage("Hiba", "Nem sikerült feldolgozni a képet: " + e.getMessage());
        }
    }

    private RequestBody createPart(String value) {
        return RequestBody.create(MediaType.parse("text/plain"), value);
    }

    private boolean isValidDate(String value) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            sdf.setLenient(false);
            sdf.parse(value);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    private String getFileName(Uri uri) {
        String result = "kivalasztott_kep";
        if ("content".equals(uri.getScheme())) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (cursor.moveToFirst() && nameIndex >= 0) {
                    result = cursor.getString(nameIndex);
                }
                cursor.close();
            }
        }
        return result;
    }

    private File createTempFileFromUri(Uri uri) throws Exception {
        String fileName = getFileName(uri);
        File tempFile = new File(getCacheDir(), fileName);

        InputStream inputStream = getContentResolver().openInputStream(uri);
        if (inputStream == null) {
            throw new Exception("Nem sikerült megnyitni a képfájlt.");
        }

        FileOutputStream outputStream = new FileOutputStream(tempFile);

        byte[] buffer = new byte[4096];
        int read;
        while ((read = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, read);
        }

        outputStream.flush();
        outputStream.close();
        inputStream.close();

        return tempFile;
    }

    private void goBack() {
        finish();
    }

    private void showMessage(String title, String message) {
        if (isFinishing() || isDestroyed()) return;

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }

    private void setupBackPressed() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    goBack();
                }
            }
        });
    }
}