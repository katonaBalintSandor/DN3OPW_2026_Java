package com.example.dn3opw_2026.activities;

import android.content.Intent;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.dn3opw_2026.R;
import com.example.dn3opw_2026.adapters.AdminLeaseAdapter;
import com.example.dn3opw_2026.model.AdminLease;
import com.example.dn3opw_2026.network.responses.AdminLeasesResponse;
import com.example.dn3opw_2026.network.responses.BaseResponse;
import com.example.dn3opw_2026.repository.AdminRepository;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminProfileActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageButton menuButton;

    private ImageView bgImage;
    private TextView titleText;
    private TextView subtitleText;

    private Button backButton;

    private Spinner pageSizeSpinner;
    private Button prevButton;
    private Button nextButton;
    private TextView pageLabel;

    private Button profileButton;
    private Button communityButton;
    private Button eventsButton;
    private Button logoutButton;

    private RecyclerView leasesRecyclerView;
    private AdminLeaseAdapter leaseAdapter;

    private final List<AdminLease> allLeases = new ArrayList<>();
    private final List<AdminLease> pagedLeases = new ArrayList<>();

    private int pageSize = 10;
    private int currentPage = 1;

    private int adminId;
    private int libraryId;
    private String firstname;
    private String lastname;
    private String username;
    private String email;

    private AdminRepository adminRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile);

        adminRepository = new AdminRepository();

        readIntentData();
        initViews();
        setupBackgroundBlur();
        setupHeader();
        setupRecyclerView();
        setupPagination();
        setupListeners();
        setupBackPressed();

        loadData();
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
    }

    private Intent createAdminIntent(Class<?> target) {
        Intent intent = new Intent(AdminProfileActivity.this, target);
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
        titleText = findViewById(R.id.titleText);
        subtitleText = findViewById(R.id.subtitleText);
        backButton = findViewById(R.id.backButton);

        pageSizeSpinner = findViewById(R.id.pageSizeSpinner);
        prevButton = findViewById(R.id.prevButton);
        nextButton = findViewById(R.id.nextButton);
        pageLabel = findViewById(R.id.pageLabel);

        profileButton = findViewById(R.id.profileButton);
        communityButton = findViewById(R.id.communityButton);
        eventsButton = findViewById(R.id.eventsButton);
        logoutButton = findViewById(R.id.logoutButton);

        leasesRecyclerView = findViewById(R.id.leasesRecyclerView);
    }

    private void setupBackgroundBlur() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            bgImage.setRenderEffect(
                    RenderEffect.createBlurEffect(12f, 12f, Shader.TileMode.CLAMP)
            );
        }
    }

    private void setupHeader() {
        titleText.setText("Itt láthatod a könyvtáradból kikölcsönzött könyveket");
        subtitleText.setText("Admin: " + lastname + " " + firstname + " – Kölcsönzések kezelése");
    }

    private void setupRecyclerView() {
        leasesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        leasesRecyclerView.setNestedScrollingEnabled(false);

        leaseAdapter = new AdminLeaseAdapter(this, pagedLeases, this::returnLease);
        leasesRecyclerView.setAdapter(leaseAdapter);
    }

    private void setupPagination() {
        List<String> values = new ArrayList<>();
        values.add("10");
        values.add("15");
        values.add("20");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                values
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pageSizeSpinner.setAdapter(adapter);
        pageSizeSpinner.setSelection(0);

        pageSizeSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                int selected = Integer.parseInt(parent.getItemAtPosition(position).toString());
                if (pageSize != selected) {
                    pageSize = selected;
                    currentPage = 1;
                    renderPage();
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });
    }

    private void setupListeners() {
        menuButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        backButton.setOnClickListener(v -> finish());

        prevButton.setOnClickListener(v -> {
            if (currentPage > 1) {
                currentPage--;
                renderPage();
            }
        });

        nextButton.setOnClickListener(v -> {
            int pages = Math.max(1, (allLeases.size() + pageSize - 1) / pageSize);
            if (currentPage < pages) {
                currentPage++;
                renderPage();
            }
        });

        profileButton.setOnClickListener(v -> drawerLayout.closeDrawer(GravityCompat.START));

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
            Intent intent = new Intent(AdminProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void loadData() {
        adminRepository.getAdminLeases(libraryId).enqueue(new Callback<AdminLeasesResponse>() {
            @Override
            public void onResponse(Call<AdminLeasesResponse> call, Response<AdminLeasesResponse> response) {
                if (response.isSuccessful()
                        && response.body() != null
                        && response.body().isSuccess()
                        && response.body().getLeases() != null) {

                    allLeases.clear();
                    allLeases.addAll(response.body().getLeases());
                    currentPage = 1;
                    renderPage();

                } else {
                    Toast.makeText(AdminProfileActivity.this, "Nem sikerült betölteni a kölcsönzéseket.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AdminLeasesResponse> call, Throwable t) {
                Toast.makeText(AdminProfileActivity.this, "Hálózati hiba: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void renderPage() {
        int total = allLeases.size();
        int pages = Math.max(1, (total + pageSize - 1) / pageSize);

        if (currentPage > pages) {
            currentPage = pages;
        }

        int start = (currentPage - 1) * pageSize;
        int end = Math.min(start + pageSize, total);

        pagedLeases.clear();
        if (start < end) {
            pagedLeases.addAll(allLeases.subList(start, end));
        }

        leaseAdapter.notifyDataSetChanged();

        pageLabel.setText(currentPage + " / " + pages);
        prevButton.setEnabled(currentPage > 1);
        nextButton.setEnabled(currentPage < pages);
    }

    private void returnLease(AdminLease lease) {
        if (!lease.isActiveLease()) {
            Toast.makeText(this, "Ez a könyv már vissza lett véve.", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Megerősítés")
                .setMessage("Biztos visszavetted a(z) '" + lease.getTitle() + "' könyvet?")
                .setPositiveButton("Igen", (dialog, which) -> returnBookNow(lease))
                .setNegativeButton("Nem", null)
                .show();
    }

    private void returnBookNow(AdminLease lease) {
        if (!lease.isActiveLease()) {
            Toast.makeText(this, "Ez a könyv már vissza lett véve.", Toast.LENGTH_SHORT).show();
            return;
        }

        adminRepository.returnBook(lease.getLease_id(), lease.getBook_id(), libraryId)
                .enqueue(new Callback<BaseResponse>() {
                    @Override
                    public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            Toast.makeText(AdminProfileActivity.this, "A könyv sikeresen visszavéve!", Toast.LENGTH_SHORT).show();
                            loadData();
                        } else {
                            Toast.makeText(AdminProfileActivity.this, "Nem sikerült a könyv visszavétele!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse> call, Throwable t) {
                        Toast.makeText(AdminProfileActivity.this, "Hálózati hiba: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
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