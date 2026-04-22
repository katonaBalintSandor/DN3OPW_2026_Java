package com.example.dn3opw_2026.activities;

import android.content.Intent;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
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
import com.example.dn3opw_2026.repository.AdminRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminBookDetailsActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageButton menuButton;

    private ImageView bgImage;
    private ImageView bookImage;

    private TextView titleText;
    private TextView authorText;
    private TextView categoryText;
    private TextView releaseDateText;
    private TextView quantityText;
    private TextView descriptionText;

    private Button modifyButton;
    private Button backDashboardButton;

    private Button profileButton;
    private Button communityButton;
    private Button eventsButton;
    private Button logoutButton;

    private int adminId;
    private int bookId;
    private int libraryId;
    private String firstname;
    private String lastname;
    private String username;
    private String email;

    private AdminRepository adminRepository;
    private Book book;

    private final ActivityResultLauncher<Intent> editBookLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK) {
                            loadBookDetails();
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_book_details);

        adminRepository = new AdminRepository();

        readIntentData();

        if (bookId <= 0) {
            finish();
            return;
        }

        initViews();
        setupBackgroundBlur();
        setupListeners();
        setupBackPressed();
        loadBookDetails();
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
        Intent intent = new Intent(AdminBookDetailsActivity.this, target);
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
        bookImage = findViewById(R.id.bookImage);

        titleText = findViewById(R.id.titleText);
        authorText = findViewById(R.id.authorText);
        categoryText = findViewById(R.id.categoryText);
        releaseDateText = findViewById(R.id.releaseDateText);
        quantityText = findViewById(R.id.quantityText);
        descriptionText = findViewById(R.id.descriptionText);

        modifyButton = findViewById(R.id.modifyButton);
        backDashboardButton = findViewById(R.id.backDashboardButton);

        profileButton = findViewById(R.id.profileButton);
        communityButton = findViewById(R.id.communityButton);
        eventsButton = findViewById(R.id.eventsButton);
        logoutButton = findViewById(R.id.logoutButton);
    }

    private void setupBackgroundBlur() {
        if (bgImage != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            bgImage.setRenderEffect(
                    RenderEffect.createBlurEffect(12f, 12f, Shader.TileMode.CLAMP)
            );
        }
    }

    private void setupListeners() {
        if (menuButton != null) {
            menuButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
        }

        if (modifyButton != null) {
            modifyButton.setOnClickListener(v -> {
                Intent intent = createAdminIntent(AdminEditBookActivity.class);
                intent.putExtra("book_id", bookId);
                intent.putExtra("library_id", libraryId);
                editBookLauncher.launch(intent);
            });
        }

        if (backDashboardButton != null) {
            backDashboardButton.setOnClickListener(v -> finish());
        }

        if (profileButton != null) {
            profileButton.setOnClickListener(v -> {
                drawerLayout.closeDrawer(GravityCompat.START);
                openAdminPage(AdminProfileActivity.class);
            });
        }

        if (communityButton != null) {
            communityButton.setOnClickListener(v -> {
                drawerLayout.closeDrawer(GravityCompat.START);
                openAdminPage(AdminCommunityActivity.class);
            });
        }

        if (eventsButton != null) {
            eventsButton.setOnClickListener(v -> {
                drawerLayout.closeDrawer(GravityCompat.START);
                openAdminPage(AdminEventActivity.class);
            });
        }

        if (logoutButton != null) {
            logoutButton.setOnClickListener(v -> {
                drawerLayout.closeDrawer(GravityCompat.START);
                Intent intent = new Intent(AdminBookDetailsActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            });
        }
    }

    private void loadBookDetails() {
        Log.d("ADMIN_BOOK_DEBUG", "bookId=" + bookId + ", libraryId=" + libraryId);

        adminRepository.getAdminBookById(bookId, libraryId).enqueue(new Callback<AdminBookDetailResponse>() {
            @Override
            public void onResponse(Call<AdminBookDetailResponse> call, Response<AdminBookDetailResponse> response) {
                Log.d("ADMIN_BOOK_DEBUG", "response code=" + response.code());
                Log.d("ADMIN_BOOK_DEBUG", "response body null? " + (response.body() == null));

                if (response.isSuccessful() && response.body() != null) {
                    Log.d("ADMIN_BOOK_DEBUG", "success=" + response.body().isSuccess());
                    Log.d("ADMIN_BOOK_DEBUG", "message=" + response.body().getMessage());
                }

                if (response.isSuccessful()
                        && response.body() != null
                        && response.body().isSuccess()
                        && response.body().getBook() != null) {
                    book = response.body().getBook();
                    bindBook();
                } else {
                    showMessage("Hiba", "Nem sikerült betölteni a könyv adatait.");
                }
            }

            @Override
            public void onFailure(Call<AdminBookDetailResponse> call, Throwable t) {
                Log.d("ADMIN_BOOK_DEBUG", "failure=" + t.getMessage());
                showMessage("Hiba", "Hálózati hiba: " + t.getMessage());
            }
        });
    }

    private void bindBook() {
        if (book == null) return;

        titleText.setText(safe(book.getTitle()));
        authorText.setText("Szerző: " + safe(book.getAuthor()));
        categoryText.setText("Kategória: " + safe(book.getCategory()));
        releaseDateText.setText("Kiadás dátuma: " + safe(book.getRelease_date()));
        quantityText.setText("Készleten: " + book.getQuantity());

        String desc = safe(book.getDescription());
        if (desc.isEmpty()) {
            desc = "Nincs leírás ehhez a könyvhöz.";
        }
        descriptionText.setText(desc);

        String imageUrl = "http://10.0.2.2/szakdolgozat_api/assets/images/books/" +
                android.net.Uri.encode(book.getPicture() == null ? "" : book.getPicture());

        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.book_placeholder)
                .error(R.drawable.book_placeholder)
                .into(bookImage);
    }

    private String safe(String s) {
        return s == null ? "" : s;
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
                if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    finish();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBookDetails();
    }
}