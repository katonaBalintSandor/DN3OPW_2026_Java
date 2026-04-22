package com.example.dn3opw_2026.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.AdapterView;
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
import com.bumptech.glide.Glide;
import com.example.dn3opw_2026.R;
import com.example.dn3opw_2026.adapters.AdminCommentAdapter;
import com.example.dn3opw_2026.model.Comment;
import com.example.dn3opw_2026.model.Topic;
import com.example.dn3opw_2026.network.responses.AdminTopicDetailsResponse;
import com.example.dn3opw_2026.network.responses.BaseResponse;
import com.example.dn3opw_2026.repository.AdminRepository;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminTopicDetailsActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageButton menuButton;

    private TextView titleHeaderText;
    private Button deleteTopicButton;
    private Button backButton;

    private Button profileButton;
    private Button communityButton;
    private Button eventsButton;
    private Button logoutButton;

    private ImageView bgImage;
    private ImageView bookImage;
    private TextView topicDetailsText;

    private RecyclerView commentRecycler;
    private TextView pageLabel;
    private Button prevButton;
    private Button nextButton;
    private Spinner pageSizeSpinner;

    private Topic topic;
    private int topicId;

    private int adminId;
    private int libraryId;
    private String firstname;
    private String lastname;
    private String username;
    private String email;

    private AdminRepository adminRepository;
    private AdminCommentAdapter commentAdapter;

    private final List<Comment> allComments = new ArrayList<>();
    private final List<Comment> pageComments = new ArrayList<>();

    private int pageSize = 10;
    private int currentPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_topic_details);

        readIntentData();

        if (topicId <= 0) {
            finish();
            return;
        }

        adminRepository = new AdminRepository();

        initViews();
        setupRecycler();
        setupSpinner();
        setupListeners();
        bindHeader();
        setupBackPressed();
        loadTopic();
    }

    private void readIntentData() {
        Intent intent = getIntent();

        topicId = intent.getIntExtra("topic_id", 0);
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
        Intent intent = new Intent(AdminTopicDetailsActivity.this, target);
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
        bookImage = findViewById(R.id.bookImage);

        titleHeaderText = findViewById(R.id.titleHeaderText);
        deleteTopicButton = findViewById(R.id.deleteTopicButton);
        backButton = findViewById(R.id.backButton);

        profileButton = findViewById(R.id.profileButton);
        communityButton = findViewById(R.id.communityButton);
        eventsButton = findViewById(R.id.eventsButton);
        logoutButton = findViewById(R.id.logoutButton);

        topicDetailsText = findViewById(R.id.topicDetailsText);

        commentRecycler = findViewById(R.id.commentRecycler);
        pageLabel = findViewById(R.id.pageLabel);
        prevButton = findViewById(R.id.prevButton);
        nextButton = findViewById(R.id.nextButton);
        pageSizeSpinner = findViewById(R.id.pageSizeSpinner);
    }

    private void setupRecycler() {
        commentRecycler.setLayoutManager(new LinearLayoutManager(this));
        commentRecycler.setNestedScrollingEnabled(false);
        commentRecycler.setHasFixedSize(false);

        commentAdapter = new AdminCommentAdapter(pageComments, this::deleteCommentPopup);
        commentRecycler.setAdapter(commentAdapter);
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
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                int selected = Integer.parseInt(parent.getItemAtPosition(position).toString());

                if (pageSize != selected) {
                    pageSize = selected;
                    currentPage = 1;
                    renderComments();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setupListeners() {
        menuButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        backButton.setOnClickListener(v -> goBack());

        prevButton.setOnClickListener(v -> prevPage());
        nextButton.setOnClickListener(v -> nextPage());

        deleteTopicButton.setOnClickListener(v -> deleteTopicPopup());

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
            new AlertDialog.Builder(this)
                    .setTitle("Kijelentkezés")
                    .setMessage("Biztosan kijelentkezel?")
                    .setPositiveButton("Igen", (dialog, which) -> {
                        Intent intent = new Intent(AdminTopicDetailsActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    })
                    .setNegativeButton("Nem", null)
                    .show();
        });
    }

    private void bindHeader() {
        titleHeaderText.setText("Admin téma részletek");
    }

    private void loadTopic() {
        adminRepository.getAdminTopicDetails(topicId).enqueue(new Callback<AdminTopicDetailsResponse>() {
            @Override
            public void onResponse(Call<AdminTopicDetailsResponse> call, Response<AdminTopicDetailsResponse> response) {
                if (response.isSuccessful()
                        && response.body() != null
                        && response.body().isSuccess()
                        && response.body().getTopic() != null) {

                    topic = response.body().getTopic();

                    allComments.clear();
                    if (response.body().getComments() != null) {
                        allComments.addAll(response.body().getComments());
                    }

                    bindTopicPanel();
                    currentPage = 1;
                    renderComments();
                } else {
                    showMessage("Hiba", "Nem sikerült betölteni a témát.");
                }
            }

            @Override
            public void onFailure(Call<AdminTopicDetailsResponse> call, Throwable t) {
                showMessage("Hiba", "Hálózati hiba: " + t.getMessage());
            }
        });
    }

    private void bindTopicPanel() {
        if (topic == null) return;

        String imageUrl = "http://10.0.2.2/szakdolgozat_api/assets/images/books/"
                + Uri.encode(topic.getBook_picture() == null ? "" : topic.getBook_picture());

        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.book_placeholder)
                .error(R.drawable.book_placeholder)
                .into(bookImage);

        String creatorName = (safe(topic.getUser_firstname()) + " " + safe(topic.getUser_lastname())).trim();

        String details =
                "Könyv:\n" + safe(topic.getBook_title()) +
                        (safe(topic.getBook_author()).isEmpty() ? "" : " — " + safe(topic.getBook_author())) + "\n\n" +
                        "Értékelés:\n" + topic.getRating() + "/5\n\n" +
                        "Szerző:\n" + creatorName + "\n\n" +
                        "Téma:\n" + safe(topic.getTopic());

        topicDetailsText.setText(details);
    }

    private void renderComments() {
        int total = allComments.size();
        int pages = Math.max(1, (total + pageSize - 1) / pageSize);

        if (currentPage > pages) {
            currentPage = pages;
        }

        pageLabel.setText(currentPage + " / " + pages);
        prevButton.setEnabled(currentPage > 1);
        nextButton.setEnabled(currentPage < pages);

        int start = (currentPage - 1) * pageSize;
        int end = Math.min(start + pageSize, total);

        pageComments.clear();
        if (start < end) {
            pageComments.addAll(allComments.subList(start, end));
        }

        commentAdapter.notifyDataSetChanged();
        commentRecycler.requestLayout();
    }

    private void nextPage() {
        int total = allComments.size();
        int pages = Math.max(1, (total + pageSize - 1) / pageSize);
        if (currentPage < pages) {
            currentPage++;
            renderComments();
        }
    }

    private void prevPage() {
        if (currentPage > 1) {
            currentPage--;
            renderComments();
        }
    }

    private void deleteTopicPopup() {
        new AlertDialog.Builder(this)
                .setTitle("Téma törlése")
                .setMessage("Biztosan törlöd a témát?\nMinden hozzászólás is törlődik!")
                .setNegativeButton("Mégse", null)
                .setPositiveButton("Törlés", (dialog, which) -> deleteTopic())
                .show();
    }

    private void deleteTopic() {
        if (topic == null) return;

        adminRepository.deleteAdminTopic(topic.getId()).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (isFinishing() || isDestroyed()) return;

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(
                            AdminTopicDetailsActivity.this,
                            "A téma és a hozzászólások törölve lettek!",
                            Toast.LENGTH_SHORT
                    ).show();

                    Intent intent = createAdminIntent(AdminCommunityActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                } else {
                    showMessage("Hiba", "Nem sikerült törölni a témát.");
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                if (isFinishing() || isDestroyed()) return;
                showMessage("Hiba", "Nem sikerült törölni a témát.");
            }
        });
    }

    private void deleteCommentPopup(int commentId) {
        new AlertDialog.Builder(this)
                .setTitle("Megerősítés")
                .setMessage("Biztosan törölni szeretnéd ezt a hozzászólást?")
                .setNegativeButton("Nem", null)
                .setPositiveButton("Igen", (dialog, which) -> deleteComment(commentId))
                .show();
    }

    private void deleteComment(int commentId) {
        adminRepository.deleteAdminComment(commentId).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    showMessage("Siker", "A hozzászólás törölve lett.");
                    loadTopic();
                } else {
                    showMessage("Hiba", "Nem sikerült törölni a hozzászólást.");
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                showMessage("Hiba", "Nem sikerült törölni a hozzászólást.");
            }
        });
    }

    private void goBack() {
        Intent intent = createAdminIntent(AdminCommunityActivity.class);
        startActivity(intent);
        finish();
    }

    private void setupBackPressed() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    goBack();
                }
            }
        });
    }

    private void showMessage(String title, String message) {
        if (isFinishing() || isDestroyed()) return;

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }
}