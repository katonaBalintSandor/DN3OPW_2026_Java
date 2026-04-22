package com.example.dn3opw_2026.activities;

import android.content.Intent;
import android.graphics.RenderEffect;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.bumptech.glide.Glide;
import com.example.dn3opw_2026.R;
import com.example.dn3opw_2026.model.Event;
import com.example.dn3opw_2026.network.responses.AdminEventDetailResponse;
import com.example.dn3opw_2026.network.responses.BaseResponse;
import com.example.dn3opw_2026.repository.AdminRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminEventDetailsActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageButton menuButton;

    private ImageView bgImage;
    private ImageView eventImage;

    private TextView titleHeaderText;
    private TextView eventDetailsText;

    private Button editEventButton;
    private Button deleteEventButton;
    private Button backButton;

    private Button profileButton;
    private Button communityButton;
    private Button eventsButton;
    private Button logoutButton;

    private int adminId;
    private int eventId;
    private int libraryId;
    private String firstname;
    private String lastname;
    private String username;
    private String email;

    private AdminRepository adminRepository;
    private Event event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_event_details);

        adminRepository = new AdminRepository();

        readIntentData();
        if (eventId <= 0) {
            finish();
            return;
        }

        initViews();
        setupBackgroundBlur();
        setupListeners();
        setupBackPressed();
        loadEvent();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadEvent();
    }

    private void readIntentData() {
        Intent intent = getIntent();
        adminId = intent.getIntExtra("admin_id", -1);
        eventId = intent.getIntExtra("event_id", -1);
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
        Intent intent = new Intent(AdminEventDetailsActivity.this, target);
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
        eventImage = findViewById(R.id.eventImage);

        titleHeaderText = findViewById(R.id.titleHeaderText);
        eventDetailsText = findViewById(R.id.eventDetailsText);

        editEventButton = findViewById(R.id.editEventButton);
        deleteEventButton = findViewById(R.id.deleteEventButton);
        backButton = findViewById(R.id.backButton);

        profileButton = findViewById(R.id.profileButton);
        communityButton = findViewById(R.id.communityButton);
        eventsButton = findViewById(R.id.eventsButton);
        logoutButton = findViewById(R.id.logoutButton);
    }

    private void setupBackgroundBlur() {
        if (bgImage != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            bgImage.setRenderEffect(
                    RenderEffect.createBlurEffect(12f, 12f, Shader.TileMode.CLAMP)
            );
        }
    }

    private void setupListeners() {
        menuButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        editEventButton.setOnClickListener(v -> {
            if (event == null) return;

            Intent intent = createAdminIntent(AdminEditEventActivity.class);
            intent.putExtra("event", event);
            intent.putExtra("library_id", event.getLibrary_id());
            startActivity(intent);
        });

        deleteEventButton.setOnClickListener(v -> deleteEventPopup());

        backButton.setOnClickListener(v -> goBack());

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
            Intent intent = new Intent(AdminEventDetailsActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
    }

    private void loadEvent() {
        adminRepository.getAdminEventById(eventId).enqueue(new Callback<AdminEventDetailResponse>() {
            @Override
            public void onResponse(Call<AdminEventDetailResponse> call, Response<AdminEventDetailResponse> response) {
                if (response.isSuccessful()
                        && response.body() != null
                        && response.body().isSuccess()
                        && response.body().getEvent() != null) {
                    event = response.body().getEvent();

                    if (event.getLibrary_id() > 0) {
                        libraryId = event.getLibrary_id();
                    }

                    bindEvent();
                } else {
                    showMessage("Hiba", "Nem sikerült betölteni az eseményt.");
                }
            }

            @Override
            public void onFailure(Call<AdminEventDetailResponse> call, Throwable t) {
                showMessage("Hiba", "Hálózati hiba: " + t.getMessage());
            }
        });
    }

    private void bindEvent() {
        if (event == null) return;

        titleHeaderText.setText("Esemény részletek");

        String imageUrl = "http://10.0.2.2/szakdolgozat_api/assets/images/events/"
                + Uri.encode(event.getPicture() == null ? "" : event.getPicture());

        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.event_placeholder)
                .error(R.drawable.event_placeholder)
                .into(eventImage);

        String details =
                "Cím:\n" + safe(event.getTitle()) + "\n\n" +
                        "Fejléc:\n" + safe(event.getHeader()) + "\n\n" +
                        "Dátum:\n" + safe(event.getDate()) + "\n\n" +
                        "Könyvtár:\n" + safe(event.getLibrary_name()) + "\n\n" +
                        "Leírás:\n" + safe(event.getDescription());

        eventDetailsText.setText(details);

        boolean isOwner = event.getAdmin_id() == adminId;
        editEventButton.setVisibility(isOwner ? android.view.View.VISIBLE : android.view.View.GONE);
        deleteEventButton.setVisibility(isOwner ? android.view.View.VISIBLE : android.view.View.GONE);
    }

    private void deleteEventPopup() {
        if (event == null) return;

        new AlertDialog.Builder(this)
                .setTitle("Esemény törlése")
                .setMessage("Biztosan törlöd az eseményt?\n\n" + event.getTitle())
                .setNegativeButton("Mégse", null)
                .setPositiveButton("Törlés", (dialog, which) -> deleteEvent())
                .show();
    }

    private void deleteEvent() {
        if (event == null) return;

        adminRepository.deleteEvent(event.getId()).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (isFinishing() || isDestroyed()) return;

                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(AdminEventDetailsActivity.this, "Az esemény törölve.", Toast.LENGTH_SHORT).show();

                    Intent intent = createAdminIntent(AdminEventActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                } else {
                    showMessage("Hiba", "Nem sikerült törölni az eseményt.");
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                if (isFinishing() || isDestroyed()) return;
                showMessage("Hiba", "Nem sikerült törölni az eseményt.");
            }
        });
    }

    private void goBack() {
        finish();
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
}