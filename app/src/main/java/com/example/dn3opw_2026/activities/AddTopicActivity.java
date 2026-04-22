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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.example.dn3opw_2026.R;
import com.example.dn3opw_2026.model.Book;
import com.example.dn3opw_2026.model.User;
import com.example.dn3opw_2026.network.responses.BaseResponse;
import com.example.dn3opw_2026.network.responses.BookResponse;
import com.example.dn3opw_2026.repository.BookRepository;
import com.example.dn3opw_2026.repository.TopicRepository;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddTopicActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageButton menuButton;

    private ImageView bgImage;

    private Spinner bookSpinner;
    private EditText topicEntry;
    private EditText descEntry;
    private Spinner ratingSpinner;

    private Button createTopicButton;
    private Button backButton;

    private Button profileButton;
    private Button communityButton;
    private Button eventsButton;
    private Button logoutButton;

    private User user;
    private BookRepository bookRepository;
    private TopicRepository topicRepository;

    private final List<Book> books = new ArrayList<>();
    private Integer selectedBookId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_topic);

        user = (User) getIntent().getSerializableExtra("user");
        if (user == null) {
            finish();
            return;
        }

        bookRepository = new BookRepository();
        topicRepository = new TopicRepository();

        initViews();
        setupBackgroundBlur();
        setupListeners();
        setupRatingSpinner();
        loadBooks();
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        menuButton = findViewById(R.id.menuButton);

        bgImage = findViewById(R.id.bgImage);

        bookSpinner = findViewById(R.id.bookSpinner);
        topicEntry = findViewById(R.id.topicEntry);
        descEntry = findViewById(R.id.descEntry);
        ratingSpinner = findViewById(R.id.ratingSpinner);

        createTopicButton = findViewById(R.id.createTopicButton);
        backButton = findViewById(R.id.backButton);

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

    private void setupListeners() {
        menuButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        createTopicButton.setOnClickListener(v -> saveTopic());
        backButton.setOnClickListener(v -> goBack());

        bookSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < books.size()) {
                    selectedBookId = books.get(position).getId();
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
                selectedBookId = null;
            }
        });

        profileButton.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            Intent intent = new Intent(AddTopicActivity.this, ProfileActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        });

        communityButton.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            Intent intent = new Intent(AddTopicActivity.this, CommunityActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
            finish();
        });

        eventsButton.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            Intent intent = new Intent(AddTopicActivity.this, EventActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        });

        logoutButton.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            Intent intent = new Intent(AddTopicActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void setupRatingSpinner() {
        List<String> ratings = new ArrayList<>();
        ratings.add("1");
        ratings.add("2");
        ratings.add("3");
        ratings.add("4");
        ratings.add("5");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                ratings
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ratingSpinner.setAdapter(adapter);
        ratingSpinner.setSelection(4);
    }

    private void loadBooks() {
        bookRepository.getAllBooks().enqueue(new Callback<BookResponse>() {
            @Override
            public void onResponse(Call<BookResponse> call, Response<BookResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {

                    books.clear();
                    if (response.body().getBooks() != null) {
                        books.addAll(response.body().getBooks());
                    }

                    List<String> labels = new ArrayList<>();
                    for (Book b : books) {
                        labels.add(b.getTitle() + " - " + b.getAuthor());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            AddTopicActivity.this,
                            android.R.layout.simple_spinner_item,
                            labels
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    bookSpinner.setAdapter(adapter);

                    if (!books.isEmpty()) {
                        selectedBookId = books.get(0).getId();
                        createTopicButton.setEnabled(true);
                    } else {
                        selectedBookId = null;
                        createTopicButton.setEnabled(false);
                        showMessage("Hiba", "Nincs elérhető könyv.");
                    }

                } else {
                    createTopicButton.setEnabled(false);
                    showMessage("Hiba", "Nem sikerült betölteni a könyveket.");
                }
            }

            @Override
            public void onFailure(Call<BookResponse> call, Throwable t) {
                createTopicButton.setEnabled(false);
                showMessage("Hiba", "Hálózati hiba: " + t.getMessage());
            }
        });
    }

    private void saveTopic() {
        if (selectedBookId == null) {
            showMessage("Hiba", "Válassz könyvet!");
            return;
        }

        String topic = topicEntry.getText().toString().trim();
        String desc = descEntry.getText().toString().trim();
        int rating = Integer.parseInt(ratingSpinner.getSelectedItem().toString());

        if (topic.isEmpty() || desc.isEmpty()) {
            showMessage("Hiba", "Minden mező kitöltése kötelező!");
            return;
        }

        topicRepository.addTopic(topic, desc, rating, selectedBookId, user.getId())
                .enqueue(new Callback<BaseResponse>() {
                    @Override
                    public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            if (response.body().isSuccess()) {
                                showMessageAndGoBack("Siker", response.body().getMessage());
                            } else {
                                showMessage("Hiba", response.body().getMessage());
                            }
                        } else {
                            String errorText = "Ismeretlen szerverhiba.";
                            try {
                                if (response.errorBody() != null) {
                                    errorText = response.errorBody().string();
                                }
                            } catch (Exception e) {
                                errorText = e.getMessage();
                            }
                            showMessage("Hiba", errorText);
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse> call, Throwable t) {
                        showMessage("Hiba", "Hálózati hiba: " + t.getMessage());
                    }
                });
    }

    private void goBack() {
        Intent intent = new Intent(AddTopicActivity.this, CommunityActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
        finish();
    }

    private void showMessage(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private void showMessageAndGoBack(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> goBack())
                .show();
    }
}