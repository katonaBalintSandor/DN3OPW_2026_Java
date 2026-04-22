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
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.dn3opw_2026.R;
import com.example.dn3opw_2026.adapters.AdminBookAdapter;
import com.example.dn3opw_2026.model.Book;
import com.example.dn3opw_2026.network.responses.BaseResponse;
import com.example.dn3opw_2026.network.responses.BookResponse;
import com.example.dn3opw_2026.network.responses.LibraryDetailResponse;
import com.example.dn3opw_2026.repository.AdminRepository;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminDashboardActivity extends AppCompatActivity {

    private static final String TAG = "ADMIN_DASHBOARD";

    private DrawerLayout drawerLayout;
    private ImageButton menuButton;

    private ImageView bgImage;
    private TextView welcomeText;
    private TextView libraryText;
    private TextView subtitleText;

    private Button profileButton;
    private Button communityButton;
    private Button eventsButton;
    private Button logoutButton;
    private Button addBookButton;

    private Button prevButton;
    private Button nextButton;
    private TextView pageLabel;
    private Spinner bookLimitSpinner;

    private RecyclerView adminBooksRecyclerView;

    private AdminBookAdapter adminBookAdapter;
    private final List<Book> allBooks = new ArrayList<>();
    private final List<Book> pagedBooks = new ArrayList<>();

    private int currentPage = 1;
    private int booksPerPage = 10;

    private int adminId;
    private int libraryId;
    private String firstname;
    private String lastname;
    private String username;
    private String email;

    private AdminRepository adminRepository;

    private final ActivityResultLauncher<Intent> addBookLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK) {
                            currentPage = 1;
                            loadBooks();
                        }
                    }
            );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        adminRepository = new AdminRepository();

        readIntentData();

        if (adminId <= 0 || libraryId <= 0) {
            Toast.makeText(this, "Hiányzó admin vagy könyvtár azonosító!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        initViews();
        setupBackgroundBlur();
        setupHeader();
        setupRecyclerView();
        setupLimitSpinner();
        setupListeners();
        setupBackPressed();

        loadLibraryInfo();
        loadBooks();
    }

    private void readIntentData() {
        Intent intent = getIntent();

        adminId = intent.getIntExtra("admin_id", -1);
        libraryId = intent.getIntExtra("library_id", -1);
        firstname = intent.getStringExtra("firstname");
        lastname = intent.getStringExtra("lastname");
        username = intent.getStringExtra("username");
        email = intent.getStringExtra("email");

        if (firstname == null) firstname = "";
        if (lastname == null) lastname = "";
        if (username == null) username = "";
        if (email == null) email = "";

        Log.d(TAG, "adminId=" + adminId + ", libraryId=" + libraryId);
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        menuButton = findViewById(R.id.menuButton);

        bgImage = findViewById(R.id.bgImage);
        welcomeText = findViewById(R.id.welcomeText);
        libraryText = findViewById(R.id.libraryText);
        subtitleText = findViewById(R.id.subtitleText);

        profileButton = findViewById(R.id.profileButton);
        communityButton = findViewById(R.id.communityButton);
        eventsButton = findViewById(R.id.eventsButton);
        logoutButton = findViewById(R.id.logoutButton);

        prevButton = findViewById(R.id.prevButton);
        nextButton = findViewById(R.id.nextButton);
        pageLabel = findViewById(R.id.pageLabel);
        bookLimitSpinner = findViewById(R.id.bookLimitSpinner);

        adminBooksRecyclerView = findViewById(R.id.adminBooksRecyclerView);
        addBookButton = findViewById(R.id.addBookButton);
    }

    private void setupBackgroundBlur() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            bgImage.setRenderEffect(
                    RenderEffect.createBlurEffect(20f, 20f, Shader.TileMode.CLAMP)
            );
        }
    }

    private void setupHeader() {
        String fullName = (firstname + " " + lastname).trim();
        welcomeText.setText("Üdvözöllek, " + fullName + "!");
        subtitleText.setText("Itt található az összes könyv a könyvtárban");
    }

    private void setupRecyclerView() {
        adminBooksRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adminBooksRecyclerView.setHasFixedSize(false);
        adminBooksRecyclerView.setNestedScrollingEnabled(true);

        adminBookAdapter = new AdminBookAdapter(this, pagedBooks, new AdminBookAdapter.OnAdminBookActionListener() {
            @Override
            public void onEdit(Book book) {
                int clickedBookId = book != null ? book.getId() : -1;
                int clickedLibraryId = book != null ? book.getLibrary_id() : -1;

                Log.d(TAG, "Clicked bookId=" + clickedBookId + ", clickedLibraryId=" + clickedLibraryId);

                if (clickedBookId <= 0 || clickedLibraryId <= 0) {
                    Toast.makeText(
                            AdminDashboardActivity.this,
                            "Érvénytelen könyv vagy könyvtár azonosító!",
                            Toast.LENGTH_LONG
                    ).show();
                    return;
                }

                Intent intent = createAdminIntent(AdminBookDetailsActivity.class);
                intent.putExtra("book_id", clickedBookId);
                intent.putExtra("library_id", clickedLibraryId);
                startActivity(intent);
            }

            @Override
            public void onQuantityEdit(Book book) {
                if (book == null || book.getId() <= 0) {
                    Toast.makeText(AdminDashboardActivity.this, "Érvénytelen könyv!", Toast.LENGTH_SHORT).show();
                    return;
                }
                showQuantityDialog(book);
            }
        });

        adminBooksRecyclerView.setAdapter(adminBookAdapter);
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
        bookLimitSpinner.setAdapter(adapter);
        bookLimitSpinner.setSelection(0);

        bookLimitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int selectedLimit = Integer.parseInt(parent.getItemAtPosition(position).toString());

                if (booksPerPage != selectedLimit) {
                    booksPerPage = selectedLimit;
                    currentPage = 1;
                    updateBookPage();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setupListeners() {
        menuButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        prevButton.setOnClickListener(v -> prevPage());
        nextButton.setOnClickListener(v -> nextPage());

        addBookButton.setOnClickListener(v -> {
            Intent intent = createAdminIntent(AdminAddBookActivity.class);
            addBookLauncher.launch(intent);
        });

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
            confirmLogout();
        });
    }

    private Intent createAdminIntent(Class<?> target) {
        Intent intent = new Intent(AdminDashboardActivity.this, target);
        intent.putExtra("admin_id", adminId);
        intent.putExtra("library_id", libraryId);
        intent.putExtra("firstname", firstname);
        intent.putExtra("lastname", lastname);
        intent.putExtra("username", username);
        intent.putExtra("email", email);
        return intent;
    }

    private void openAdminPage(Class<?> target) {
        Intent intent = createAdminIntent(target);
        startActivity(intent);
    }

    private void loadLibraryInfo() {
        adminRepository.getAdminLibrary(libraryId).enqueue(new Callback<LibraryDetailResponse>() {
            @Override
            public void onResponse(Call<LibraryDetailResponse> call, Response<LibraryDetailResponse> response) {
                if (response.isSuccessful()
                        && response.body() != null
                        && response.body().isSuccess()
                        && response.body().getLibrary() != null) {
                    libraryText.setText("Te a(z) " + response.body().getLibrary().getName() + " adminja vagy");
                } else {
                    libraryText.setText("Te admin vagy");
                }
            }

            @Override
            public void onFailure(Call<LibraryDetailResponse> call, Throwable t) {
                libraryText.setText("Te admin vagy");
            }
        });
    }

    private void loadBooks() {
        adminRepository.getAdminBooks(libraryId).enqueue(new Callback<BookResponse>() {
            @Override
            public void onResponse(Call<BookResponse> call, Response<BookResponse> response) {
                if (response.isSuccessful()
                        && response.body() != null
                        && response.body().isSuccess()
                        && response.body().getBooks() != null) {

                    allBooks.clear();
                    allBooks.addAll(response.body().getBooks());

                    Log.d(TAG, "Books loaded: " + allBooks.size());

                    currentPage = 1;
                    updateBookPage();
                } else {
                    Toast.makeText(AdminDashboardActivity.this, "Nem sikerült betölteni a könyveket.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BookResponse> call, Throwable t) {
                Toast.makeText(AdminDashboardActivity.this, "Hálózati hiba: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateBookPage() {
        int total = allBooks.size();
        int maxPage = Math.max(1, (total + booksPerPage - 1) / booksPerPage);

        if (currentPage > maxPage) {
            currentPage = maxPage;
        }

        int start = (currentPage - 1) * booksPerPage;
        int end = Math.min(start + booksPerPage, total);

        pagedBooks.clear();
        if (start < end) {
            pagedBooks.addAll(allBooks.subList(start, end));
        }

        adminBookAdapter.notifyDataSetChanged();

        pageLabel.setText(currentPage + ". oldal / " + maxPage);
        prevButton.setEnabled(currentPage > 1);
        nextButton.setEnabled(currentPage < maxPage);
    }

    private void nextPage() {
        int total = allBooks.size();
        int maxPage = Math.max(1, (total + booksPerPage - 1) / booksPerPage);

        if (currentPage < maxPage) {
            currentPage++;
            updateBookPage();
        }
    }

    private void prevPage() {
        if (currentPage > 1) {
            currentPage--;
            updateBookPage();
        }
    }

    private void showQuantityDialog(Book book) {
        final EditText input = new EditText(this);
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        input.setText(String.valueOf(book.getQuantity()));
        input.setSelection(input.getText().length());

        new AlertDialog.Builder(this)
                .setTitle("Mennyiség módosítása")
                .setMessage("Könyv: " + book.getTitle())
                .setView(input)
                .setPositiveButton("Mentés", (dialog, which) -> {
                    String value = input.getText().toString().trim();

                    if (value.isEmpty()) {
                        Toast.makeText(this, "Adj meg egy mennyiséget.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    int newQuantity = Integer.parseInt(value);
                    updateBookQuantity(book.getId(), newQuantity);
                })
                .setNegativeButton("Mégse", null)
                .show();
    }

    private void updateBookQuantity(int bookId, int quantity) {
        adminRepository.updateBookQuantity(libraryId, bookId, quantity).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(AdminDashboardActivity.this, "Mennyiség módosítva!", Toast.LENGTH_SHORT).show();
                    loadBooks();
                } else {
                    Toast.makeText(AdminDashboardActivity.this, "Nem sikerült a frissítés.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Toast.makeText(AdminDashboardActivity.this, "Hálózati hiba: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void confirmLogout() {
        new AlertDialog.Builder(this)
                .setTitle("Kijelentkezés")
                .setMessage("Biztosan kijelentkezel?")
                .setPositiveButton("Igen", (dialog, which) -> {
                    Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .setNegativeButton("Nem", null)
                .show();
    }

    private void setupBackPressed() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    confirmLogout();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadLibraryInfo();
        loadBooks();
    }
}