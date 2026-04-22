package com.example.dn3opw_2026.activities;

import android.content.Intent;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.bumptech.glide.Glide;
import com.example.dn3opw_2026.R;
import com.example.dn3opw_2026.model.Book;
import com.example.dn3opw_2026.model.User;
import com.example.dn3opw_2026.network.responses.BookDetailResponse;
import com.example.dn3opw_2026.repository.BookRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookActivity extends AppCompatActivity {

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

    private Button leaseButton;
    private Button backButton;
    private Button backHomeButton;

    private Button profileButton, communityButton, eventsButton, logoutButton;

    private User user;
    private int bookId;
    private int libraryId;

    private BookRepository bookRepository;
    private Book book;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book);

        user = (User) getIntent().getSerializableExtra("user");
        bookId = getIntent().getIntExtra("book_id", 0);
        libraryId = getIntent().getIntExtra("library_id", 0);

        if (bookId <= 0 || libraryId <= 0) {
            finish();
            return;
        }

        bookRepository = new BookRepository();

        initViews();
        setupBackgroundBlur();
        setupListeners();
        loadBook();
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

        leaseButton = findViewById(R.id.leaseButton);
        backButton = findViewById(R.id.backButton);
        backHomeButton = findViewById(R.id.backHomeButton);

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

    private void setupListeners() {
        menuButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        leaseButton.setOnClickListener(v -> leaseBook());

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(BookActivity.this, LibraryActivity.class);
            intent.putExtra("user", user);
            intent.putExtra("library_id", libraryId);
            startActivity(intent);
            finish();
        });

        backHomeButton.setOnClickListener(v -> {
            Intent intent = new Intent(BookActivity.this, HomeActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
            finish();
        });

        profileButton.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            Intent intent = new Intent(BookActivity.this, ProfileActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        });

        communityButton.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            Intent intent = new Intent(BookActivity.this, CommunityActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        });

        eventsButton.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            Intent intent = new Intent(BookActivity.this, EventActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        });

        logoutButton.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            Intent intent = new Intent(BookActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void loadBook() {
        bookRepository.getBookDetails(bookId, libraryId).enqueue(new Callback<BookDetailResponse>() {
            @Override
            public void onResponse(Call<BookDetailResponse> call, Response<BookDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    book = response.body().getBook();
                    bindBook();
                } else {
                    showMessage("Hiba", "Nem sikerült betölteni a könyv adatait.");
                }
            }

            @Override
            public void onFailure(Call<BookDetailResponse> call, Throwable t) {
                showMessage("Hiba", "Hálózati hiba: " + t.getMessage());
            }
        });
    }

    private void bindBook() {
        if (book == null) return;

        titleText.setText(book.getTitle());
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

        if (book.getQuantity() <= 0) {
            leaseButton.setEnabled(false);
            leaseButton.setText("Nincs készleten");
            leaseButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                    getResources().getColor(android.R.color.darker_gray)
            ));
        } else {
            leaseButton.setEnabled(true);
            leaseButton.setText("Kölcsönzés");
            leaseButton.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                    0xFF2ECC71
            ));
        }
    }

    private void leaseBook() {
        if (user == null || book == null) return;

        bookRepository.leaseBook(user.getId(), libraryId, bookId).enqueue(new Callback<com.example.dn3opw_2026.network.responses.BaseResponse>() {
            @Override
            public void onResponse(Call<com.example.dn3opw_2026.network.responses.BaseResponse> call,
                                   Response<com.example.dn3opw_2026.network.responses.BaseResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        showMessage("Sikeres kölcsönzés",
                                "Sikeresen kikölcsönözted a(z) '" + book.getTitle() + "' könyvet.");
                        refreshView();
                    } else {
                        showMessage("Nincs készleten", "Sajnáljuk, ez a könyv jelenleg nem elérhető.");
                    }
                } else {
                    showMessage("Hiba", "Nem sikerült a kölcsönzés.");
                }
            }

            @Override
            public void onFailure(Call<com.example.dn3opw_2026.network.responses.BaseResponse> call, Throwable t) {
                showMessage("Hiba", "Hiba történt: " + t.getMessage());
            }
        });
    }

    private void refreshView() {
        loadBook();
    }

    private void showMessage(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }
}