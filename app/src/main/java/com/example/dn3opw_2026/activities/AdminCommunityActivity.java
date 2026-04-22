package com.example.dn3opw_2026.activities;

import android.content.Intent;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.dn3opw_2026.R;
import com.example.dn3opw_2026.adapters.AdminCommunityAdapter;
import com.example.dn3opw_2026.model.AdminCommunityTopic;
import com.example.dn3opw_2026.network.responses.AdminCommunityTopicsResponse;
import com.example.dn3opw_2026.repository.AdminRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminCommunityActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageButton menuButton;

    private ImageView bgImage;
    private EditText searchEntry;
    private Button searchButton;
    private Button backButton;

    private Button profileButton;
    private Button communityButton;
    private Button eventsButton;
    private Button logoutButton;

    private Spinner itemsPerPageSpinner;
    private Button prevButton;
    private Button nextButton;
    private TextView pageLabel;

    private RecyclerView topicsRecyclerView;
    private AdminCommunityAdapter adapter;

    private final List<AdminCommunityTopic> allTopics = new ArrayList<>();
    private final List<AdminCommunityTopic> filteredTopics = new ArrayList<>();
    private final List<AdminCommunityTopic> pagedTopics = new ArrayList<>();

    private int currentPage = 1;
    private int itemsPerPage = 10;

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
        setContentView(R.layout.activity_admin_community);

        adminRepository = new AdminRepository();

        readIntentData();
        initViews();
        setupBackgroundBlur();
        setupRecyclerView();
        setupSpinner();
        setupListeners();
        setupBackPressed();

        loadTopics();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTopics();
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
        Intent intent = new Intent(AdminCommunityActivity.this, target);
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
        searchEntry = findViewById(R.id.searchEntry);
        searchButton = findViewById(R.id.searchButton);
        backButton = findViewById(R.id.backButton);

        profileButton = findViewById(R.id.profileButton);
        communityButton = findViewById(R.id.communityButton);
        eventsButton = findViewById(R.id.eventsButton);
        logoutButton = findViewById(R.id.logoutButton);

        itemsPerPageSpinner = findViewById(R.id.itemsPerPageSpinner);
        prevButton = findViewById(R.id.prevButton);
        nextButton = findViewById(R.id.nextButton);
        pageLabel = findViewById(R.id.pageLabel);

        topicsRecyclerView = findViewById(R.id.topicsRecyclerView);
    }

    private void setupBackgroundBlur() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            bgImage.setRenderEffect(
                    RenderEffect.createBlurEffect(12f, 12f, Shader.TileMode.CLAMP)
            );
        }
    }

    private void setupRecyclerView() {
        topicsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        topicsRecyclerView.setNestedScrollingEnabled(false);

        adapter = new AdminCommunityAdapter(this, pagedTopics, topic -> openTopic(topic.getId()));
        topicsRecyclerView.setAdapter(adapter);
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
        itemsPerPageSpinner.setAdapter(spinnerAdapter);
        itemsPerPageSpinner.setSelection(0);

        itemsPerPageSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                int selected = Integer.parseInt(parent.getItemAtPosition(position).toString());
                if (itemsPerPage != selected) {
                    itemsPerPage = selected;
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

        searchButton.setOnClickListener(v -> searchTopics());

        prevButton.setOnClickListener(v -> {
            if (currentPage > 1) {
                currentPage--;
                renderPage();
            }
        });

        nextButton.setOnClickListener(v -> {
            int pages = Math.max(1, (filteredTopics.size() + itemsPerPage - 1) / itemsPerPage);
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

        communityButton.setOnClickListener(v -> drawerLayout.closeDrawer(GravityCompat.START));

        eventsButton.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            openAdminPage(AdminEventActivity.class);
        });

        logoutButton.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            Intent intent = new Intent(AdminCommunityActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void loadTopics() {
        adminRepository.getAdminCommunityTopics().enqueue(new Callback<AdminCommunityTopicsResponse>() {
            @Override
            public void onResponse(Call<AdminCommunityTopicsResponse> call, Response<AdminCommunityTopicsResponse> response) {
                if (response.isSuccessful()
                        && response.body() != null
                        && response.body().isSuccess()
                        && response.body().getTopics() != null) {

                    allTopics.clear();
                    allTopics.addAll(response.body().getTopics());

                    filteredTopics.clear();
                    filteredTopics.addAll(allTopics);

                    currentPage = 1;
                    renderPage();

                } else {
                    Toast.makeText(AdminCommunityActivity.this, "Nem sikerült betölteni a témákat.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AdminCommunityTopicsResponse> call, Throwable t) {
                Toast.makeText(AdminCommunityActivity.this, "Hálózati hiba: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void searchTopics() {
        String query = searchEntry.getText().toString().trim().toLowerCase(Locale.ROOT);

        filteredTopics.clear();

        if (query.isEmpty()) {
            filteredTopics.addAll(allTopics);
        } else {
            for (AdminCommunityTopic topic : allTopics) {
                boolean matchesTopic = topic.getTopic() != null &&
                        topic.getTopic().toLowerCase(Locale.ROOT).contains(query);

                boolean matchesBook = topic.getBook_title() != null &&
                        topic.getBook_title().toLowerCase(Locale.ROOT).contains(query);

                if (matchesTopic || matchesBook) {
                    filteredTopics.add(topic);
                }
            }
        }

        currentPage = 1;
        renderPage();
    }

    private void renderPage() {
        int total = filteredTopics.size();
        int pages = Math.max(1, (total + itemsPerPage - 1) / itemsPerPage);

        if (currentPage > pages) {
            currentPage = pages;
        }

        int start = (currentPage - 1) * itemsPerPage;
        int end = Math.min(start + itemsPerPage, total);

        pagedTopics.clear();
        if (start < end) {
            pagedTopics.addAll(filteredTopics.subList(start, end));
        }

        adapter.notifyDataSetChanged();

        pageLabel.setText(currentPage + " / " + pages);
        prevButton.setEnabled(currentPage > 1);
        nextButton.setEnabled(currentPage < pages);
    }

    private void openTopic(int topicId) {
        Intent intent = createAdminIntent(AdminTopicDetailsActivity.class);
        intent.putExtra("topic_id", topicId);
        startActivity(intent);
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