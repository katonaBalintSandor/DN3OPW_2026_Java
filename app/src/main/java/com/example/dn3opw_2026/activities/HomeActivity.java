package com.example.dn3opw_2026.activities;

import android.content.Intent;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.dn3opw_2026.R;
import com.example.dn3opw_2026.adapters.LibraryAdapter;
import com.example.dn3opw_2026.adapters.RecommendationAdapter;
import com.example.dn3opw_2026.model.Book;
import com.example.dn3opw_2026.model.Library;
import com.example.dn3opw_2026.model.User;
import com.example.dn3opw_2026.network.responses.LibrariesResponse;
import com.example.dn3opw_2026.network.responses.RecommendationsResponse;
import com.example.dn3opw_2026.repository.LibraryRepository;
import com.example.dn3opw_2026.repository.RecommendationRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageButton menuButton;

    private ImageView bgImage;
    private TextView welcomeText;
    private EditText searchEntry;
    private Button searchButton, profileButton, communityButton, eventsButton, logoutButton;
    private Button prevButton, nextButton;
    private TextView pageLabel;
    private Spinner libraryLimitSpinner;

    private RecyclerView libraryRecycler;
    private RecyclerView recommendationRecycler;

    private LibraryAdapter libraryAdapter;
    private RecommendationAdapter recommendationAdapter;

    private final List<Library> allLibraries = new ArrayList<>();
    private final List<Library> filteredLibraries = new ArrayList<>();
    private final List<Book> recommendedBooks = new ArrayList<>();

    private int libraryPage = 1;
    private int libraryLimit = 10;

    private User user;

    private LibraryRepository libraryRepository;
    private RecommendationRepository recommendationRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        libraryRepository = new LibraryRepository();
        recommendationRepository = new RecommendationRepository();

        user = (User) getIntent().getSerializableExtra("user");
        if (user == null) {
            user = new User(1, "Default", "User", "defaultuser", "default@example.com");
        }

        initViews();
        setupBackgroundBlur();
        setupRecyclerViews();
        setupLimitSpinner();
        setupHeader();
        setupListeners();

        loadLibraries();
        loadRecommendations();
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        menuButton = findViewById(R.id.menuButton);

        bgImage = findViewById(R.id.bgImage);
        welcomeText = findViewById(R.id.welcomeText);
        searchEntry = findViewById(R.id.searchEntry);
        searchButton = findViewById(R.id.searchButton);

        profileButton = findViewById(R.id.profileButton);
        communityButton = findViewById(R.id.communityButton);
        eventsButton = findViewById(R.id.eventsButton);
        logoutButton = findViewById(R.id.logoutButton);

        prevButton = findViewById(R.id.prevButton);
        nextButton = findViewById(R.id.nextButton);
        pageLabel = findViewById(R.id.pageLabel);
        libraryLimitSpinner = findViewById(R.id.libraryLimitSpinner);

        libraryRecycler = findViewById(R.id.libraryRecycler);
        recommendationRecycler = findViewById(R.id.recommendationRecycler);
    }

    private void setupBackgroundBlur() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            bgImage.setRenderEffect(
                    RenderEffect.createBlurEffect(20f, 20f, Shader.TileMode.CLAMP)
            );
        }
    }

    private void setupHeader() {
        String name = user.getFirstname() + " " + user.getLastname();
        welcomeText.setText("Üdvözöllek, " + name + "!");
    }

    private LinearLayoutManager createNonScrollableLayoutManager() {
        return new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
    }

    private void setupRecyclerViews() {
        libraryRecycler.setLayoutManager(createNonScrollableLayoutManager());
        recommendationRecycler.setLayoutManager(createNonScrollableLayoutManager());

        libraryRecycler.setNestedScrollingEnabled(false);
        recommendationRecycler.setNestedScrollingEnabled(false);

        libraryRecycler.setHasFixedSize(false);
        recommendationRecycler.setHasFixedSize(false);

        libraryAdapter = new LibraryAdapter(new ArrayList<>(), library -> {
            Intent intent = new Intent(HomeActivity.this, LibraryActivity.class);
            intent.putExtra("library_id", library.getId());
            intent.putExtra("user", user);
            startActivity(intent);
        });

        recommendationAdapter = new RecommendationAdapter(new ArrayList<>(), book -> {
            Intent intent = new Intent(HomeActivity.this, BookActivity.class);
            intent.putExtra("book_id", book.getId());
            intent.putExtra("library_id", book.getLibrary_id());
            intent.putExtra("user", user);
            startActivity(intent);
        });

        libraryRecycler.setAdapter(libraryAdapter);
        recommendationRecycler.setAdapter(recommendationAdapter);
    }

    private void setupLimitSpinner() {
        List<String> limits = new ArrayList<>();
        limits.add("10");
        limits.add("15");
        limits.add("20");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                limits
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        libraryLimitSpinner.setAdapter(adapter);
        libraryLimitSpinner.setSelection(0);

        libraryLimitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int selectedLimit = Integer.parseInt(parent.getItemAtPosition(position).toString());

                if (libraryLimit != selectedLimit) {
                    libraryLimit = selectedLimit;
                    libraryPage = 1;
                    updateLibraryPage();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setupListeners() {
        menuButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        searchButton.setOnClickListener(v -> searchLibraries());

        prevButton.setOnClickListener(v -> prevPage());
        nextButton.setOnClickListener(v -> nextPage());

        profileButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
            drawerLayout.closeDrawer(GravityCompat.START);
        });

        communityButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CommunityActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
            drawerLayout.closeDrawer(GravityCompat.START);
        });

        eventsButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, EventActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
            drawerLayout.closeDrawer(GravityCompat.START);
        });

        logoutButton.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            logout();
        });
    }

    private void loadLibraries() {
        libraryRepository.getLibraries().enqueue(new Callback<LibrariesResponse>() {
            @Override
            public void onResponse(Call<LibrariesResponse> call, Response<LibrariesResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    allLibraries.clear();
                    allLibraries.addAll(response.body().getLibraries());

                    filteredLibraries.clear();
                    filteredLibraries.addAll(allLibraries);

                    libraryPage = 1;
                    updateLibraryPage();
                }
            }

            @Override
            public void onFailure(Call<LibrariesResponse> call, Throwable t) {
                showMessage("Hiba", "Nem sikerült betölteni a könyvtárakat.");
            }
        });
    }

    private void searchLibraries() {
        String query = searchEntry.getText().toString().trim().toLowerCase(Locale.ROOT);

        filteredLibraries.clear();

        if (query.isEmpty()) {
            filteredLibraries.addAll(allLibraries);
        } else {
            for (Library lib : allLibraries) {
                boolean matchesName = lib.getName() != null &&
                        lib.getName().toLowerCase(Locale.ROOT).contains(query);

                boolean matchesCity = lib.getCity() != null &&
                        lib.getCity().toLowerCase(Locale.ROOT).contains(query);

                if (matchesName || matchesCity) {
                    filteredLibraries.add(lib);
                }
            }
        }

        libraryPage = 1;
        updateLibraryPage();
    }

    private void updateLibraryPage() {
        int total = filteredLibraries.size();
        int maxPage = Math.max(1, (total + libraryLimit - 1) / libraryLimit);

        if (libraryPage > maxPage) {
            libraryPage = maxPage;
        }

        int start = (libraryPage - 1) * libraryLimit;
        int end = Math.min(start + libraryLimit, total);

        List<Library> pageItems = new ArrayList<>();
        if (start < end) {
            pageItems.addAll(filteredLibraries.subList(start, end));
        }

        libraryAdapter.updateData(pageItems);
        libraryRecycler.post(libraryRecycler::requestLayout);

        pageLabel.setText(libraryPage + ". oldal / " + maxPage);
        prevButton.setEnabled(libraryPage > 1);
        nextButton.setEnabled(libraryPage < maxPage);
    }

    private void nextPage() {
        int total = filteredLibraries.size();
        int maxPage = Math.max(1, (total + libraryLimit - 1) / libraryLimit);

        if (libraryPage < maxPage) {
            libraryPage++;
            updateLibraryPage();
        }
    }

    private void prevPage() {
        if (libraryPage > 1) {
            libraryPage--;
            updateLibraryPage();
        }
    }

    private void loadRecommendations() {
        recommendationRepository.getRecommendations(user.getId(), 5).enqueue(new Callback<RecommendationsResponse>() {
            @Override
            public void onResponse(Call<RecommendationsResponse> call, Response<RecommendationsResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    recommendedBooks.clear();
                    recommendedBooks.addAll(response.body().getBooks());

                    Log.d("REC_DEBUG", "Recommendations received: " + recommendedBooks.size());

                    recommendationAdapter.updateData(recommendedBooks);
                    recommendationRecycler.post(recommendationRecycler::requestLayout);
                } else {
                    showMessage("Hiba", "Nem sikerült betölteni az ajánlott könyveket.");
                }
            }

            @Override
            public void onFailure(Call<RecommendationsResponse> call, Throwable t) {
                showMessage("Hiba", "Nem sikerült betölteni az ajánlott könyveket.");
            }
        });
    }

    private void logout() {
        new AlertDialog.Builder(this)
                .setTitle("Kijelentkezés")
                .setMessage("Biztosan kijelentkezel?")
                .setPositiveButton("Igen", (dialog, which) -> {
                    Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
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
}