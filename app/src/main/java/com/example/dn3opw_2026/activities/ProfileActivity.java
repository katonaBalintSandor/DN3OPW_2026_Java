package com.example.dn3opw_2026.activities;

import android.content.Intent;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.example.dn3opw_2026.adapters.LeasedBookAdapter;
import com.example.dn3opw_2026.model.Book;
import com.example.dn3opw_2026.model.User;
import com.example.dn3opw_2026.network.responses.BaseResponse;
import com.example.dn3opw_2026.network.responses.LeasedBooksResponse;
import com.example.dn3opw_2026.repository.BookRepository;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageButton menuButton;
    private ImageView bgImage;

    private TextView nameText, usernameText, emailText, pageLabel;
    private Button prevButton, nextButton, changePasswordButton, backButton;
    private Button profileButton, communityButton, eventsButton, logoutButton;

    private RecyclerView recyclerView;
    private LeasedBookAdapter adapter;

    private final List<Book> allBooks = new ArrayList<>();
    private final List<Book> pageBooks = new ArrayList<>();
    private final List<Book> filteredBooks = new ArrayList<>();

    private int page = 1;
    private int pageSize = 10;

    private User user;
    private BookRepository bookRepository;

    private CheckBox pendingCheckBox;
    private Spinner pageSizeSpinner;
    private boolean showPendingOnly = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        user = (User) getIntent().getSerializableExtra("user");
        if (user == null) {
            finish();
            return;
        }

        bookRepository = new BookRepository();

        initViews();
        setupBackgroundBlur();
        setupRecycler();
        setupPageSizeSpinner();
        setupListeners();
        loadUserData();
        loadBooks();
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        menuButton = findViewById(R.id.menuButton);
        bgImage = findViewById(R.id.bgImage);

        nameText = findViewById(R.id.nameText);
        usernameText = findViewById(R.id.usernameText);
        emailText = findViewById(R.id.emailText);
        pageLabel = findViewById(R.id.pageLabel);

        prevButton = findViewById(R.id.prevButton);
        nextButton = findViewById(R.id.nextButton);
        changePasswordButton = findViewById(R.id.changePasswordButton);
        backButton = findViewById(R.id.backButton);

        profileButton = findViewById(R.id.profileButton);
        communityButton = findViewById(R.id.communityButton);
        eventsButton = findViewById(R.id.eventsButton);
        logoutButton = findViewById(R.id.logoutButton);

        recyclerView = findViewById(R.id.recyclerView);
        pendingCheckBox = findViewById(R.id.pendingCheckBox);
        pageSizeSpinner = findViewById(R.id.pageSizeSpinner);
    }

    private void setupBackgroundBlur() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            bgImage.setRenderEffect(
                    RenderEffect.createBlurEffect(20f, 20f, Shader.TileMode.CLAMP)
            );
        }
    }

    private void setupRecycler() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(false);

        adapter = new LeasedBookAdapter(pageBooks, this::returnBook);
        recyclerView.setAdapter(adapter);
    }

    private void setupPageSizeSpinner() {
        List<String> options = new ArrayList<>();
        options.add("10");
        options.add("15");
        options.add("20");

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                options
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pageSizeSpinner.setAdapter(spinnerAdapter);

        pageSizeSpinner.setSelection(0);

        pageSizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int selectedSize = Integer.parseInt(parent.getItemAtPosition(position).toString());

                if (pageSize != selectedSize) {
                    pageSize = selectedSize;
                    page = 1;
                    updatePage();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setupListeners() {
        menuButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        backButton.setOnClickListener(v -> finish());

        prevButton.setOnClickListener(v -> prevPage());
        nextButton.setOnClickListener(v -> nextPage());

        changePasswordButton.setOnClickListener(v -> showChangePasswordDialog());

        profileButton.setOnClickListener(v -> drawerLayout.closeDrawer(GravityCompat.START));

        pendingCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            showPendingOnly = isChecked;
            applyFilter();
        });

        communityButton.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            Intent intent = new Intent(ProfileActivity.this, CommunityActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        });

        eventsButton.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            Intent intent = new Intent(ProfileActivity.this, EventActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        });

        logoutButton.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            new AlertDialog.Builder(this)
                    .setTitle("Kijelentkezés")
                    .setMessage("Biztosan kijelentkezel?")
                    .setPositiveButton("Igen", (dialog, which) -> {
                        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    })
                    .setNegativeButton("Nem", null)
                    .show();
        });
    }

    private void loadUserData() {
        nameText.setText(user.getFirstname() + " " + user.getLastname());
        usernameText.setText("Felhasználónév: " + user.getUsername());
        emailText.setText("Email: " + user.getEmail());
    }

    private void loadBooks() {
        bookRepository.getLeasedBooks(user.getId()).enqueue(new Callback<LeasedBooksResponse>() {
            @Override
            public void onResponse(Call<LeasedBooksResponse> call, Response<LeasedBooksResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    allBooks.clear();
                    if (response.body().getBooks() != null) {
                        allBooks.addAll(response.body().getBooks());
                    }
                    applyFilter();
                }
            }

            @Override
            public void onFailure(Call<LeasedBooksResponse> call, Throwable t) {
                showMessage("Hiba", "Nem sikerült betölteni a kölcsönzött könyveket.");
            }
        });
    }

    private void applyFilter() {
        filteredBooks.clear();

        if (showPendingOnly) {
            for (Book b : allBooks) {
                if (b.getReturned_date() == null || b.getReturned_date().trim().isEmpty()) {
                    filteredBooks.add(b);
                }
            }
        } else {
            filteredBooks.addAll(allBooks);
        }

        page = 1;
        updatePage();
    }

    private void updatePage() {
        int total = filteredBooks.size();
        int maxPage = Math.max(1, (total + pageSize - 1) / pageSize);

        if (page > maxPage) {
            page = maxPage;
        }

        int start = (page - 1) * pageSize;
        int end = Math.min(start + pageSize, total);

        pageBooks.clear();
        if (start < end) {
            pageBooks.addAll(filteredBooks.subList(start, end));
        }

        adapter.notifyDataSetChanged();

        pageLabel.setText(page + " / " + maxPage);
        prevButton.setEnabled(page > 1);
        nextButton.setEnabled(page < maxPage);
    }

    private void nextPage() {
        int maxPage = Math.max(1, (filteredBooks.size() + pageSize - 1) / pageSize);
        if (page < maxPage) {
            page++;
            updatePage();
        }
    }

    private void prevPage() {
        if (page > 1) {
            page--;
            updatePage();
        }
    }

    private void returnBook(Book book) {
        bookRepository.returnBook(book.getLease_id(), book.getId(), book.getLibrary_id())
                .enqueue(new Callback<BaseResponse>() {
                    @Override
                    public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            BaseResponse result = response.body();

                            if (result.isSuccess()) {
                                loadBooks();
                            } else {
                                showMessage("Hiba", result.getMessage());
                            }
                        } else {
                            showMessage("Hiba", "Nem sikerült visszaadni a könyvet.");
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse> call, Throwable t) {
                        showMessage("Hiba", "Hálózati hiba: " + t.getMessage());
                    }
                });
    }

    private void showMessage(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private void showChangePasswordDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_password, null);

        final EditText newPasswordInput = dialogView.findViewById(R.id.newPasswordInput);
        final EditText confirmPasswordInput = dialogView.findViewById(R.id.confirmPasswordInput);

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Új jelszó beállítása")
                .setView(dialogView)
                .setPositiveButton("Mentés", null)
                .setNegativeButton("Mégse", (d, which) -> d.dismiss())
                .create();

        dialog.show();

        Button saveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        saveButton.setOnClickListener(v -> {
            newPasswordInput.clearFocus();
            confirmPasswordInput.clearFocus();

            String pw1 = String.valueOf(newPasswordInput.getText()).trim();
            String pw2 = String.valueOf(confirmPasswordInput.getText()).trim();

            if (pw1.length() == 0 || pw2.length() == 0) {
                showMessage("Hiányzó adat", "Kérlek töltsd ki az összes mezőt.");
                return;
            }

            if (pw1.length() < 8) {
                showMessage("Rövid jelszó", "Legalább 8 karakter szükséges.");
                return;
            }

            boolean hasLower = pw1.matches(".*[a-z].*");
            boolean hasUpper = pw1.matches(".*[A-Z].*");
            boolean hasSpecial = pw1.matches(".*[^a-zA-Z0-9].*");

            if (!hasLower || !hasUpper || !hasSpecial) {
                showMessage("Gyenge jelszó",
                        "A jelszónak tartalmaznia kell kisbetűt, nagybetűt és speciális karaktert.");
                return;
            }

            if (!pw1.equals(pw2)) {
                showMessage("Hiba", "A jelszavak nem egyeznek!");
                return;
            }

            bookRepository.updatePassword(user.getId(), pw1).enqueue(new Callback<BaseResponse>() {
                @Override
                public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        BaseResponse result = response.body();

                        if (result.isSuccess()) {
                            showMessage("Siker", "A jelszó sikeresen módosítva.");
                            dialog.dismiss();
                        } else {
                            showMessage("Hiba", result.getMessage());
                        }
                    } else {
                        showMessage("Hiba", "Szerverhiba történt. HTTP code: " + response.code());
                    }
                }

                @Override
                public void onFailure(Call<BaseResponse> call, Throwable t) {
                    showMessage("Hiba", "Nem sikerült módosítani a jelszót.\n" + t.getMessage());
                }
            });
        });
    }
}