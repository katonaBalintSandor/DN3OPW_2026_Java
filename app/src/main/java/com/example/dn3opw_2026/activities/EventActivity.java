package com.example.dn3opw_2026.activities;

import android.content.Intent;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.example.dn3opw_2026.adapters.EventAdapter;
import com.example.dn3opw_2026.model.Event;
import com.example.dn3opw_2026.model.User;
import com.example.dn3opw_2026.network.responses.EventsResponse;
import com.example.dn3opw_2026.repository.EventRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageButton menuButton;

    private ImageView bgImage;
    private EditText searchEntry;
    private Button searchButton;
    private Button backButton;
    private Button prevButton;
    private Button nextButton;
    private Button profileButton, communityButton, eventsButton, logoutButton;
    private TextView pageLabel;
    private Spinner pageSizeSpinner;
    private RecyclerView recyclerView;

    private EventAdapter eventAdapter;

    private final List<Event> allEvents = new ArrayList<>();
    private final List<Event> filteredEvents = new ArrayList<>();
    private final List<Event> pageEvents = new ArrayList<>();

    private int currentPage = 1;
    private int pageSize = 10;

    private User user;
    private EventRepository eventRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        user = (User) getIntent().getSerializableExtra("user");
        eventRepository = new EventRepository();

        initViews();
        setupBackgroundBlur();
        setupRecycler();
        setupSpinner();
        setupListeners();
        loadEvents();
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        menuButton = findViewById(R.id.menuButton);

        bgImage = findViewById(R.id.bgImage);
        searchEntry = findViewById(R.id.searchEntry);
        searchButton = findViewById(R.id.searchButton);
        backButton = findViewById(R.id.backButton);
        prevButton = findViewById(R.id.prevButton);
        nextButton = findViewById(R.id.nextButton);
        pageLabel = findViewById(R.id.pageLabel);
        pageSizeSpinner = findViewById(R.id.pageSizeSpinner);
        recyclerView = findViewById(R.id.recyclerView);

        profileButton = findViewById(R.id.profileButton);
        communityButton = findViewById(R.id.communityButton);
        eventsButton = findViewById(R.id.eventsButton);
        logoutButton = findViewById(R.id.logoutButton);
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
        recyclerView.setHasFixedSize(false);

        eventAdapter = new EventAdapter(pageEvents, event -> {
            Intent intent = new Intent(EventActivity.this, EventDetailsActivity.class);
            intent.putExtra("user", user);
            intent.putExtra("event", event);
            intent.putExtra("from_admin", false);
            startActivity(intent);
        });

        recyclerView.setAdapter(eventAdapter);
    }

    private void setupSpinner() {
        List<String> options = new ArrayList<>();
        options.add("10");
        options.add("15");
        options.add("20");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                options
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        pageSizeSpinner.setAdapter(adapter);
        pageSizeSpinner.setSelection(0);

        pageSizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int selected = Integer.parseInt(parent.getItemAtPosition(position).toString());
                if (pageSize != selected) {
                    pageSize = selected;
                    currentPage = 1;
                    renderPage();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setupListeners() {
        menuButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        backButton.setOnClickListener(v -> {
            Intent intent = new Intent(EventActivity.this, HomeActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
            finish();
        });

        searchButton.setOnClickListener(v -> filterEvents());

        searchEntry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterEvents();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        prevButton.setOnClickListener(v -> prevPage());
        nextButton.setOnClickListener(v -> nextPage());

        profileButton.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            Intent intent = new Intent(EventActivity.this, ProfileActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        });

        communityButton.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            Intent intent = new Intent(EventActivity.this, CommunityActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        });

        eventsButton.setOnClickListener(v -> drawerLayout.closeDrawer(GravityCompat.START));

        logoutButton.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            new AlertDialog.Builder(this)
                    .setTitle("Kijelentkezés")
                    .setMessage("Biztosan kijelentkezel?")
                    .setPositiveButton("Igen", (dialog, which) -> {
                        Intent intent = new Intent(EventActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    })
                    .setNegativeButton("Nem", null)
                    .show();
        });
    }

    private void loadEvents() {
        eventRepository.getAllEvents().enqueue(new Callback<EventsResponse>() {
            @Override
            public void onResponse(Call<EventsResponse> call, Response<EventsResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    allEvents.clear();
                    if (response.body().getEvents() != null) {
                        allEvents.addAll(response.body().getEvents());
                    }

                    filteredEvents.clear();
                    filteredEvents.addAll(allEvents);

                    currentPage = 1;
                    renderPage();
                }
            }

            @Override
            public void onFailure(Call<EventsResponse> call, Throwable t) {
                showMessage("Hiba", "Nem sikerült betölteni az eseményeket.");
            }
        });
    }

    private void filterEvents() {
        String query = searchEntry.getText().toString().toLowerCase(Locale.ROOT).trim();

        filteredEvents.clear();

        if (query.isEmpty()) {
            filteredEvents.addAll(allEvents);
        } else {
            for (Event e : allEvents) {
                String title = e.getTitle() == null ? "" : e.getTitle().toLowerCase(Locale.ROOT);
                String header = e.getHeader() == null ? "" : e.getHeader().toLowerCase(Locale.ROOT);
                String library = e.getLibrary_name() == null ? "" : e.getLibrary_name().toLowerCase(Locale.ROOT);

                if (title.contains(query) || header.contains(query) || library.contains(query)) {
                    filteredEvents.add(e);
                }
            }
        }

        currentPage = 1;
        renderPage();
    }

    private void renderPage() {
        int total = filteredEvents.size();
        int pages = Math.max(1, (total + pageSize - 1) / pageSize);

        if (currentPage > pages) {
            currentPage = pages;
        }

        int start = (currentPage - 1) * pageSize;
        int end = Math.min(start + pageSize, total);

        pageEvents.clear();
        if (start < end) {
            pageEvents.addAll(filteredEvents.subList(start, end));
        }

        eventAdapter.notifyDataSetChanged();
        recyclerView.requestLayout();

        pageLabel.setText(currentPage + " / " + pages);
        prevButton.setEnabled(currentPage > 1);
        nextButton.setEnabled(currentPage < pages);
    }

    private void nextPage() {
        int total = filteredEvents.size();
        int pages = Math.max(1, (total + pageSize - 1) / pageSize);

        if (currentPage < pages) {
            currentPage++;
            renderPage();
        }
    }

    private void prevPage() {
        if (currentPage > 1) {
            currentPage--;
            renderPage();
        }
    }

    private void showMessage(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }
}