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
import com.example.dn3opw_2026.adapters.TopicAdapter;
import com.example.dn3opw_2026.model.Topic;
import com.example.dn3opw_2026.model.User;
import com.example.dn3opw_2026.network.responses.TopicsResponse;
import com.example.dn3opw_2026.repository.TopicRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommunityActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageButton menuButton;

    private ImageView bgImage;
    private EditText searchEntry;
    private Button searchButton, backButton, addTopicButton;
    private Button prevButton, nextButton;
    private Button profileButton, communityButton, eventsButton, logoutButton;
    private TextView pageLabel;
    private Spinner itemsPerPageSpinner;

    private RecyclerView topicRecycler;
    private TopicAdapter topicAdapter;

    private final List<Topic> allTopics = new ArrayList<>();
    private final List<Topic> filteredTopics = new ArrayList<>();

    private int currentPage = 1;
    private int itemsPerPage = 10;

    private User user;
    private TopicRepository topicRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);

        user = (User) getIntent().getSerializableExtra("user");
        topicRepository = new TopicRepository();

        initViews();
        setupBackgroundBlur();
        setupRecycler();
        setupSpinner();
        setupListeners();
        loadTopics();
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        menuButton = findViewById(R.id.menuButton);

        bgImage = findViewById(R.id.bgImage);
        searchEntry = findViewById(R.id.searchEntry);
        searchButton = findViewById(R.id.searchButton);
        backButton = findViewById(R.id.backButton);
        addTopicButton = findViewById(R.id.addTopicButton);

        prevButton = findViewById(R.id.prevButton);
        nextButton = findViewById(R.id.nextButton);
        pageLabel = findViewById(R.id.pageLabel);
        itemsPerPageSpinner = findViewById(R.id.itemsPerPageSpinner);

        topicRecycler = findViewById(R.id.topicRecycler);

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
        topicRecycler.setLayoutManager(new LinearLayoutManager(this));
        topicRecycler.setNestedScrollingEnabled(false);

        topicAdapter = new TopicAdapter(new ArrayList<>(), topic -> {
            Intent intent = new Intent(CommunityActivity.this, TopicDetailsActivity.class);
            intent.putExtra("user", user);
            intent.putExtra("topic_id", topic.getId());
            startActivity(intent);
        });

        topicRecycler.setAdapter(topicAdapter);
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
        itemsPerPageSpinner.setAdapter(adapter);
        itemsPerPageSpinner.setSelection(0);

        itemsPerPageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int selected = Integer.parseInt(parent.getItemAtPosition(position).toString());

                if (itemsPerPage != selected) {
                    itemsPerPage = selected;
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
            Intent intent = new Intent(CommunityActivity.this, HomeActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
            finish();
        });

        searchButton.setOnClickListener(v -> searchTopics());

        prevButton.setOnClickListener(v -> prevPage());
        nextButton.setOnClickListener(v -> nextPage());

        addTopicButton.setOnClickListener(v -> {
            Intent intent = new Intent(CommunityActivity.this, AddTopicActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        });

        profileButton.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            Intent intent = new Intent(CommunityActivity.this, ProfileActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        });

        communityButton.setOnClickListener(v -> drawerLayout.closeDrawer(GravityCompat.START));

        eventsButton.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            Intent intent = new Intent(CommunityActivity.this, EventActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        });

        logoutButton.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            new AlertDialog.Builder(this)
                    .setTitle("Kijelentkezés")
                    .setMessage("Biztosan kijelentkezel?")
                    .setPositiveButton("Igen", (dialog, which) -> {
                        Intent intent = new Intent(CommunityActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    })
                    .setNegativeButton("Nem", null)
                    .show();
        });
    }

    private void loadTopics() {
        topicRepository.getAllTopics().enqueue(new Callback<TopicsResponse>() {
            @Override
            public void onResponse(Call<TopicsResponse> call, Response<TopicsResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    allTopics.clear();
                    if (response.body().getTopics() != null) {
                        allTopics.addAll(response.body().getTopics());
                    }

                    filteredTopics.clear();
                    filteredTopics.addAll(allTopics);

                    currentPage = 1;
                    renderPage();
                }
            }

            @Override
            public void onFailure(Call<TopicsResponse> call, Throwable t) {
                showMessage("Hiba", "Nem sikerült betölteni a témákat.");
            }
        });
    }

    private void searchTopics() {
        String query = searchEntry.getText().toString().trim().toLowerCase(Locale.ROOT);

        filteredTopics.clear();

        if (query.isEmpty()) {
            filteredTopics.addAll(allTopics);
        } else {
            for (Topic topic : allTopics) {
                String topicText = topic.getTopic() == null ? "" : topic.getTopic().toLowerCase(Locale.ROOT);
                String bookTitle = topic.getBook_title() == null ? "" : topic.getBook_title().toLowerCase(Locale.ROOT);

                if (topicText.contains(query) || bookTitle.contains(query)) {
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

        List<Topic> pageItems = new ArrayList<>();
        if (start < end) {
            pageItems.addAll(filteredTopics.subList(start, end));
        }

        topicAdapter.updateData(pageItems);

        pageLabel.setText(currentPage + " / " + pages);
        prevButton.setEnabled(currentPage > 1);
        nextButton.setEnabled(currentPage < pages);
    }

    private void nextPage() {
        int total = filteredTopics.size();
        int pages = Math.max(1, (total + itemsPerPage - 1) / itemsPerPage);

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