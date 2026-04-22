package com.example.dn3opw_2026.activities;

import android.content.Intent;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.example.dn3opw_2026.adapters.AdminEventAdapter;
import com.example.dn3opw_2026.model.Event;
import com.example.dn3opw_2026.network.responses.AdminEventsResponse;
import com.example.dn3opw_2026.network.responses.BaseResponse;
import com.example.dn3opw_2026.repository.AdminRepository;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminEventActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageButton menuButton;

    private ImageView bgImage;

    private Button addEventButton;
    private CheckBox ownEventsCheckBox;
    private Button backButton;

    private Button profileButton;
    private Button communityButton;
    private Button eventsButton;
    private Button logoutButton;

    private Spinner eventsPerPageSpinner;
    private Button prevButton;
    private Button nextButton;
    private TextView pageLabel;

    private RecyclerView eventRecyclerView;
    private AdminEventAdapter adapter;

    private final List<Event> allEvents = new ArrayList<>();
    private final List<Event> filteredEvents = new ArrayList<>();
    private final List<Event> pagedEvents = new ArrayList<>();

    private boolean filterMyEvents = false;
    private int currentPage = 1;
    private int eventsPerPage = 10;

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
        setContentView(R.layout.activity_admin_event);

        adminRepository = new AdminRepository();

        readIntentData();
        initViews();
        setupBackgroundBlur();
        setupRecyclerView();
        setupSpinner();
        setupListeners();
        setupBackPressed();

        loadEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadEvents();
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
        Intent intent = new Intent(AdminEventActivity.this, target);
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

        addEventButton = findViewById(R.id.addEventButton);
        ownEventsCheckBox = findViewById(R.id.ownEventsCheckBox);
        backButton = findViewById(R.id.backButton);

        profileButton = findViewById(R.id.profileButton);
        communityButton = findViewById(R.id.communityButton);
        eventsButton = findViewById(R.id.eventsButton);
        logoutButton = findViewById(R.id.logoutButton);

        eventsPerPageSpinner = findViewById(R.id.eventsPerPageSpinner);
        prevButton = findViewById(R.id.prevButton);
        nextButton = findViewById(R.id.nextButton);
        pageLabel = findViewById(R.id.pageLabel);

        eventRecyclerView = findViewById(R.id.eventRecyclerView);
    }

    private void setupBackgroundBlur() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            bgImage.setRenderEffect(
                    RenderEffect.createBlurEffect(12f, 12f, Shader.TileMode.CLAMP)
            );
        }
    }

    private void setupRecyclerView() {
        eventRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventRecyclerView.setNestedScrollingEnabled(false);

        adapter = new AdminEventAdapter(this, pagedEvents, adminId, new AdminEventAdapter.OnAdminEventActionListener() {
            @Override
            public void onOpen(Event event) {
                openEvent(event);
            }

            @Override
            public void onEdit(Event event) {
                openEditEvent(event);
            }

            @Override
            public void onDelete(Event event) {
                deleteEvent(event);
            }
        });

        eventRecyclerView.setAdapter(adapter);
    }

    private void setupSpinner() {
        List<String> values = new ArrayList<>();
        values.add("10");
        values.add("15");
        values.add("20");

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                values
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        eventsPerPageSpinner.setAdapter(spinnerAdapter);
        eventsPerPageSpinner.setSelection(0);

        eventsPerPageSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                int selected = Integer.parseInt(parent.getItemAtPosition(position).toString());
                if (eventsPerPage != selected) {
                    eventsPerPage = selected;
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

        addEventButton.setOnClickListener(v -> openAddEvent());

        ownEventsCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            filterMyEvents = isChecked;
            applyFilter();
        });

        prevButton.setOnClickListener(v -> {
            if (currentPage > 1) {
                currentPage--;
                renderPage();
            }
        });

        nextButton.setOnClickListener(v -> {
            int pages = Math.max(1, (filteredEvents.size() + eventsPerPage - 1) / eventsPerPage);
            if (currentPage < pages) {
                currentPage++;
                renderPage();
            }
        });

        backButton.setOnClickListener(v -> finish());

        profileButton.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            openAdminPage(AdminProfileActivity.class);
        });

        communityButton.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            openAdminPage(AdminCommunityActivity.class);
        });

        eventsButton.setOnClickListener(v -> drawerLayout.closeDrawer(GravityCompat.START));

        logoutButton.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            Intent intent = new Intent(AdminEventActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void loadEvents() {
        adminRepository.getAdminEvents().enqueue(new Callback<AdminEventsResponse>() {
            @Override
            public void onResponse(Call<AdminEventsResponse> call, Response<AdminEventsResponse> response) {
                if (response.isSuccessful()
                        && response.body() != null
                        && response.body().isSuccess()
                        && response.body().getEvents() != null) {

                    allEvents.clear();
                    allEvents.addAll(response.body().getEvents());
                    applyFilter();

                } else {
                    Toast.makeText(AdminEventActivity.this, "Nem sikerült betölteni az eseményeket.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AdminEventsResponse> call, Throwable t) {
                Toast.makeText(AdminEventActivity.this, "Hálózati hiba: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void applyFilter() {
        filteredEvents.clear();

        if (filterMyEvents) {
            for (Event event : allEvents) {
                if (event.getAdmin_id() == adminId) {
                    filteredEvents.add(event);
                }
            }
        } else {
            filteredEvents.addAll(allEvents);
        }

        currentPage = 1;
        renderPage();
    }

    private void renderPage() {
        int total = filteredEvents.size();
        int pages = Math.max(1, (total + eventsPerPage - 1) / eventsPerPage);

        if (currentPage > pages) {
            currentPage = pages;
        }
        if (currentPage < 1) {
            currentPage = 1;
        }

        int start = (currentPage - 1) * eventsPerPage;
        int end = Math.min(start + eventsPerPage, total);

        pagedEvents.clear();
        if (start < end) {
            pagedEvents.addAll(filteredEvents.subList(start, end));
        }

        adapter.notifyDataSetChanged();

        pageLabel.setText(currentPage + " / " + pages);
        prevButton.setEnabled(currentPage > 1);
        nextButton.setEnabled(currentPage < pages);
    }

    private void openEvent(Event event) {
        Intent intent = createAdminIntent(AdminEventDetailsActivity.class);
        intent.putExtra("event_id", event.getId());
        intent.putExtra("library_id", event.getLibrary_id());
        startActivity(intent);
    }

    private void openEditEvent(Event event) {
        Intent intent = createAdminIntent(AdminEditEventActivity.class);
        intent.putExtra("event", event);
        intent.putExtra("library_id", event.getLibrary_id());
        startActivity(intent);
    }

    private void openAddEvent() {
        Intent intent = createAdminIntent(AdminAddEventActivity.class);
        startActivity(intent);
    }

    private void deleteEvent(Event event) {
        new AlertDialog.Builder(this)
                .setTitle("Törlés megerősítése")
                .setMessage("Biztosan törölni szeretnéd az eseményt?\n\n" + event.getTitle())
                .setNegativeButton("Mégse", null)
                .setPositiveButton("Törlés", (dialog, which) -> doDeleteEvent(event))
                .show();
    }

    private void doDeleteEvent(Event event) {
        adminRepository.deleteEvent(event.getId()).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(AdminEventActivity.this, "Az esemény törölve.", Toast.LENGTH_SHORT).show();
                    loadEvents();
                } else {
                    Toast.makeText(AdminEventActivity.this, "Nem sikerült törölni az eseményt.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                Toast.makeText(AdminEventActivity.this, "Hálózati hiba: " + t.getMessage(), Toast.LENGTH_LONG).show();
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