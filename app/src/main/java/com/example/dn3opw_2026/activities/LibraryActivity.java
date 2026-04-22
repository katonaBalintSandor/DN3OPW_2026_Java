package com.example.dn3opw_2026.activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.dn3opw_2026.R;
import com.example.dn3opw_2026.adapters.LibraryBooksAdapter;
import com.example.dn3opw_2026.model.Book;
import com.example.dn3opw_2026.model.Library;
import com.example.dn3opw_2026.model.User;
import com.example.dn3opw_2026.network.responses.LibraryDetailResponse;
import com.example.dn3opw_2026.repository.LibraryRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LibraryActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageButton menuButton;
    private ImageView bgImage;

    private ImageView libraryImage;
    private TextView libraryTitle;
    private TextView librarySubtitle;

    private EditText searchEntry;
    private Button searchButton;
    private Button backButton;

    private AutoCompleteTextView pageSizeSelect;
    private Button prevButton;
    private Button nextButton;
    private TextView pageLabel;

    private Button profileButton, communityButton, eventsButton, logoutButton;

    private RecyclerView booksRecycler;
    private LibraryBooksAdapter booksAdapter;

    private final List<Book> allBooks = new ArrayList<>();
    private final List<Book> filteredBooks = new ArrayList<>();
    private final List<Book> pageBooks = new ArrayList<>();

    private int currentPage = 1;
    private int booksPerPage = 10;

    private int libraryId;
    private User user;
    private Library library;

    private LibraryRepository libraryRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        user = (User) getIntent().getSerializableExtra("user");
        libraryId = getIntent().getIntExtra("library_id", 0);

        if (libraryId <= 0) {
            finish();
            return;
        }

        libraryRepository = new LibraryRepository();

        initViews();
        setupBackgroundBlur();
        setupDrawer();
        setupRecycler();
        setupPageSizeMenu();
        setupListeners();
        loadLibraryDetails();
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        menuButton = findViewById(R.id.menuButton);
        bgImage = findViewById(R.id.bgImage);

        libraryImage = findViewById(R.id.libraryImage);
        libraryTitle = findViewById(R.id.libraryTitle);
        librarySubtitle = findViewById(R.id.librarySubtitle);

        searchEntry = findViewById(R.id.searchEntry);
        searchButton = findViewById(R.id.searchButton);
        backButton = findViewById(R.id.backButton);

        pageSizeSelect = findViewById(R.id.pageSizeSelect);
        prevButton = findViewById(R.id.prevButton);
        nextButton = findViewById(R.id.nextButton);
        pageLabel = findViewById(R.id.pageLabel);

        profileButton = findViewById(R.id.profileButton);
        communityButton = findViewById(R.id.communityButton);
        eventsButton = findViewById(R.id.eventsButton);
        logoutButton = findViewById(R.id.logoutButton);

        booksRecycler = findViewById(R.id.booksRecycler);
    }

    private void setupBackgroundBlur() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            bgImage.setRenderEffect(
                    RenderEffect.createBlurEffect(20f, 20f, Shader.TileMode.CLAMP)
            );
        }
    }

    private void setupDrawer() {
        menuButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
    }

    private void setupRecycler() {
        booksRecycler.setLayoutManager(new LinearLayoutManager(this));
        booksRecycler.setNestedScrollingEnabled(false);
        booksRecycler.setHasFixedSize(false);

        booksAdapter = new LibraryBooksAdapter(pageBooks, book -> {
            Intent intent = new Intent(LibraryActivity.this, BookActivity.class);
            intent.putExtra("user", user);
            intent.putExtra("book_id", book.getId());
            intent.putExtra("library_id", libraryId);
            startActivity(intent);
        });

        booksRecycler.setAdapter(booksAdapter);
    }

    private void setupPageSizeMenu() {
        String[] sizes = {"10", "15", "20"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, sizes);
        pageSizeSelect.setAdapter(adapter);
        pageSizeSelect.setText("10", false);
    }

    private void setupListeners() {
        searchButton.setOnClickListener(v -> performSearch());

        backButton.setOnClickListener(v -> finish());

        prevButton.setOnClickListener(v -> prevPage());
        nextButton.setOnClickListener(v -> nextPage());

        pageSizeSelect.setOnItemClickListener((parent, view, position, id) -> {
            try {
                booksPerPage = Integer.parseInt(pageSizeSelect.getText().toString().trim());
            } catch (Exception e) {
                booksPerPage = 10;
            }
            currentPage = 1;
            refresh();
        });

        libraryImage.setOnClickListener(v -> {
            if (library != null && library.getPicture() != null && !library.getPicture().isEmpty()) {
                String imageUrl = getLibraryImageUrl(library.getPicture());
                showFullImage(imageUrl);
            }
        });

        profileButton.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            Intent intent = new Intent(LibraryActivity.this, ProfileActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        });

        communityButton.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            Intent intent = new Intent(LibraryActivity.this, CommunityActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        });

        eventsButton.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            Intent intent = new Intent(LibraryActivity.this, EventActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        });

        logoutButton.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            new AlertDialog.Builder(this)
                    .setTitle("Kijelentkezés")
                    .setMessage("Biztosan kijelentkezel?")
                    .setPositiveButton("Igen", (dialog, which) -> {
                        Intent intent = new Intent(LibraryActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    })
                    .setNegativeButton("Nem", null)
                    .show();
        });
    }

    private void loadLibraryDetails() {
        libraryRepository.getLibraryDetails(libraryId).enqueue(new Callback<LibraryDetailResponse>() {
            @Override
            public void onResponse(Call<LibraryDetailResponse> call, Response<LibraryDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    library = response.body().getLibrary();

                    allBooks.clear();
                    if (response.body().getBooks() != null) {
                        allBooks.addAll(response.body().getBooks());
                    }

                    filteredBooks.clear();
                    filteredBooks.addAll(allBooks);

                    bindLibraryHeader();
                    currentPage = 1;
                    refresh();
                }
            }

            @Override
            public void onFailure(Call<LibraryDetailResponse> call, Throwable t) {
                showMessage("Hiba", "Nem sikerült betölteni a könyvtár adatait.");
            }
        });
    }

    private void bindLibraryHeader() {
        if (library == null) return;

        libraryTitle.setText(library.getName() + " - " + library.getCity());
        librarySubtitle.setText("Keresd meg a könyvet cím, szerző vagy kategória alapján");

        String imageUrl = getLibraryImageUrl(library.getPicture());

        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.library_placeholder)
                .error(R.drawable.library_placeholder)
                .into(libraryImage);
    }

    private void performSearch() {
        String query = searchEntry.getText().toString().trim().toLowerCase(Locale.ROOT);

        filteredBooks.clear();

        if (query.isEmpty()) {
            filteredBooks.addAll(allBooks);
        } else {
            for (Book b : allBooks) {
                String haystack = (
                        safe(b.getTitle()) + " " +
                                safe(b.getAuthor()) + " " +
                                safe(b.getCategory())
                ).toLowerCase(Locale.ROOT);

                if (haystack.contains(query)) {
                    filteredBooks.add(b);
                }
            }
        }

        currentPage = 1;
        refresh();
    }

    private void refresh() {
        int totalBooks = filteredBooks.size();
        int pages = Math.max(1, (totalBooks + booksPerPage - 1) / booksPerPage);

        if (currentPage > pages) currentPage = pages;

        int start = (currentPage - 1) * booksPerPage;
        int end = Math.min(start + booksPerPage, totalBooks);

        pageBooks.clear();
        if (start < end) {
            pageBooks.addAll(filteredBooks.subList(start, end));
        }

        booksAdapter.updateData(pageBooks);
        booksRecycler.requestLayout();

        pageLabel.setText(currentPage + " / " + pages);
        prevButton.setEnabled(currentPage > 1);
        nextButton.setEnabled(currentPage < pages);
    }

    private void nextPage() {
        int total = filteredBooks.size();
        int pages = Math.max(1, (total + booksPerPage - 1) / booksPerPage);

        if (currentPage < pages) {
            currentPage++;
            refresh();
        }
    }

    private void prevPage() {
        if (currentPage > 1) {
            currentPage--;
            refresh();
        }
    }

    private void showFullImage(String imageUrl) {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_full_image);

        ImageView fullImage = dialog.findViewById(R.id.fullImage);
        Button closeButton = dialog.findViewById(R.id.closeImageButton);

        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.library_placeholder)
                .error(R.drawable.library_placeholder)
                .into(fullImage);

        closeButton.setOnClickListener(v -> dialog.dismiss());

        if (dialog.getWindow() != null) {
            dialog.getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
        }

        dialog.show();
    }

    private String getLibraryImageUrl(String picture) {
        return "http://10.0.2.2/szakdolgozat_api/assets/images/libraries/" +
                Uri.encode(picture == null ? "" : picture);
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
}