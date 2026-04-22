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
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.bumptech.glide.Glide;
import com.example.dn3opw_2026.R;
import com.example.dn3opw_2026.model.Book;
import com.example.dn3opw_2026.network.responses.AdminBookDetailResponse;
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

public class AdminEditBookActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageButton menuButton;
    private ImageView bgImage;
    private ImageView imagePreview;

    private TextView headerTitle;
    private TextView adminText;

    private EditText titleEntry;
    private EditText authorEntry;
    private EditText categoryEntry;
    private EditText releaseEntry;
    private EditText descriptionEntry;

    private Button selectImageButton;
    private Button saveImageButton;
    private TextView imageLabel;
    private Button submitButton;
    private Button backButton;

    private Button profileButton;
    private Button communityButton;
    private Button eventsButton;
    private Button logoutButton;

    private Uri selectedImageUri;
    private boolean imageReadyToUpload = false;
    private String currentPicture = "";

    private int adminId;
    private int bookId;
    private int libraryId;
    private String firstname;
    private String lastname;
    private String username;
    private String email;

    private AdminRepository adminRepository;
    private Book book;

    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    imageLabel.setText(getFileName(uri));
                    imageReadyToUpload = false;
                    saveImageButton.setEnabled(true);
                    saveImageButton.setText("Mentés könyvtárba");

                    Glide.with(this)
                            .load(uri)
                            .placeholder(R.drawable.book_placeholder)
                            .error(R.drawable.book_placeholder)
                            .into(imagePreview);
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_edit_book);

        adminRepository = new AdminRepository();

        readIntentData();
        initViews();
        setupBackgroundBlur();
        setupHeader();
        setupListeners();
        setupBackPressed();
        loadBook();
    }

    private void readIntentData() {
        Intent intent = getIntent();
        adminId = intent.getIntExtra("admin_id", -1);
        bookId = intent.getIntExtra("book_id", -1);
        libraryId = intent.getIntExtra("library_id", -1);
        firstname = intent.getStringExtra("firstname");
        lastname = intent.getStringExtra("lastname");
        username = intent.getStringExtra("username");
        email = intent.getStringExtra("email");

        if (firstname == null) firstname = "";
        if (lastname == null) lastname = "";
        if (username == null) username = "";
        if (email == null) email = "";
    }

    private Intent createAdminIntent(Class<?> target) {
        Intent intent = new Intent(AdminEditBookActivity.this, target);
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
        imagePreview = findViewById(R.id.imagePreview);

        headerTitle = findViewById(R.id.headerTitle);
        adminText = findViewById(R.id.adminText);

        titleEntry = findViewById(R.id.titleEntry);
        authorEntry = findViewById(R.id.authorEntry);
        categoryEntry = findViewById(R.id.categoryEntry);
        releaseEntry = findViewById(R.id.releaseEntry);
        descriptionEntry = findViewById(R.id.descriptionEntry);

        selectImageButton = findViewById(R.id.selectImageButton);
        saveImageButton = findViewById(R.id.saveImageButton);
        imageLabel = findViewById(R.id.imageLabel);
        submitButton = findViewById(R.id.submitButton);
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

    private void setupHeader() {
        headerTitle.setText("Könyv módosítása");
        adminText.setText("Admin: " + lastname + " " + firstname);
    }

    private void setupListeners() {
        menuButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        selectImageButton.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));

        saveImageButton.setOnClickListener(v -> {
            if (selectedImageUri == null) {
                showMessage("Nincs kép", "Először válassz ki egy képet!");
                return;
            }

            imageReadyToUpload = true;
            saveImageButton.setEnabled(false);
            saveImageButton.setText("Mentve");
            showMessage("Siker", "Az új kép mentésre kész.");
        });

        submitButton.setOnClickListener(v -> submitUpdate());

        backButton.setOnClickListener(v -> finish());

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
            Intent intent = new Intent(AdminEditBookActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void loadBook() {
        adminRepository.getAdminBookById(bookId, libraryId).enqueue(new Callback<AdminBookDetailResponse>() {
            @Override
            public void onResponse(Call<AdminBookDetailResponse> call, Response<AdminBookDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess() && response.body().getBook() != null) {
                    book = response.body().getBook();
                    bindBook();
                } else {
                    showMessage("Hiba", "Nem sikerült betölteni a könyv adatait.");
                }
            }

            @Override
            public void onFailure(Call<AdminBookDetailResponse> call, Throwable t) {
                showMessage("Hiba", "Hálózati hiba: " + t.getMessage());
            }
        });
    }

    private void bindBook() {
        if (book == null) return;

        currentPicture = book.getPicture() == null ? "" : book.getPicture();

        titleEntry.setText(book.getTitle() == null ? "" : book.getTitle());
        authorEntry.setText(book.getAuthor() == null ? "" : book.getAuthor());
        categoryEntry.setText(book.getCategory() == null ? "" : book.getCategory());
        releaseEntry.setText(book.getRelease_date() == null ? "" : book.getRelease_date());
        descriptionEntry.setText(book.getDescription() == null ? "" : book.getDescription());

        if (currentPicture.isEmpty()) {
            imageLabel.setText("Jelenlegi kép: nincs");
            Glide.with(this)
                    .load(R.drawable.book_placeholder)
                    .into(imagePreview);
        } else {
            imageLabel.setText("Jelenlegi kép: " + currentPicture);

            String imageUrl = "http://10.0.2.2/szakdolgozat_api/assets/images/books/" + Uri.encode(currentPicture);

            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.book_placeholder)
                    .error(R.drawable.book_placeholder)
                    .into(imagePreview);
        }
    }

    private void submitUpdate() {
        String title = titleEntry.getText().toString().trim();
        String author = authorEntry.getText().toString().trim();
        String category = categoryEntry.getText().toString().trim();
        String releaseDate = releaseEntry.getText().toString().trim();
        String description = descriptionEntry.getText().toString().trim();

        if (title.isEmpty() || author.isEmpty() || category.isEmpty() || releaseDate.isEmpty() || description.isEmpty()) {
            showMessage("Hiányzó adat", "Kérlek, tölts ki minden mezőt!");
            return;
        }

        if (!isValidDate(releaseDate)) {
            showMessage("Hibás dátum", "A kiadás dátumát így add meg: ÉÉÉÉ-HH-NN");
            return;
        }

        try {
            MultipartBody.Part imagePart;

            if (selectedImageUri != null) {
                if (!imageReadyToUpload) {
                    showMessage("Kép nincs elmentve", "Az új képet előbb mentsd el a Mentés könyvtárba gombbal!");
                    return;
                }

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
            } else {
                RequestBody emptyBody = RequestBody.create(MediaType.parse("text/plain"), "");
                imagePart = MultipartBody.Part.createFormData("image", "", emptyBody);
            }

            adminRepository.updateAdminBook(
                    createPart(String.valueOf(bookId)),
                    createPart(title),
                    createPart(author),
                    createPart(category),
                    createPart(releaseDate),
                    createPart(description),
                    createPart(currentPicture),
                    imagePart
            ).enqueue(new Callback<BaseResponse>() {
                @Override
                public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        String message = "A könyv módosítása sikertelen!";
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
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    finish();
                }
            }
        });
    }
}