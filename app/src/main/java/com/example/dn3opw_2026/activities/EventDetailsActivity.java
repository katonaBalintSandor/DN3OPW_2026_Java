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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.bumptech.glide.Glide;
import com.example.dn3opw_2026.R;
import com.example.dn3opw_2026.model.Event;
import com.example.dn3opw_2026.model.User;

public class EventDetailsActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageButton menuButton;

    private ImageView bgImage;
    private ImageView eventImage;

    private TextView titleText;
    private TextView headerText;
    private TextView dateText;
    private TextView libraryText;
    private TextView descriptionText;

    private Button backButton;
    private Button profileButton, communityButton, eventsButton, logoutButton;

    private User user;
    private Event event;
    private boolean fromAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        user = (User) getIntent().getSerializableExtra("user");
        event = (Event) getIntent().getSerializableExtra("event");
        fromAdmin = getIntent().getBooleanExtra("from_admin", false);

        if (event == null) {
            finish();
            return;
        }

        initViews();
        setupBackgroundBlur();
        setupListeners();
        bindEvent();
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        menuButton = findViewById(R.id.menuButton);

        bgImage = findViewById(R.id.bgImage);
        eventImage = findViewById(R.id.eventImage);

        titleText = findViewById(R.id.titleText);
        headerText = findViewById(R.id.headerText);
        dateText = findViewById(R.id.dateText);
        libraryText = findViewById(R.id.libraryText);
        descriptionText = findViewById(R.id.descriptionText);

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

        backButton.setOnClickListener(v -> goBack());

        profileButton.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            if (user != null) {
                Intent intent = new Intent(EventDetailsActivity.this, ProfileActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });

        communityButton.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            if (user != null) {
                Intent intent = new Intent(EventDetailsActivity.this, CommunityActivity.class);
                intent.putExtra("user", user);
                startActivity(intent);
            }
        });

        eventsButton.setOnClickListener(v -> drawerLayout.closeDrawer(GravityCompat.START));

        logoutButton.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            new AlertDialog.Builder(this)
                    .setTitle("Kijelentkezés")
                    .setMessage("Biztosan kijelentkezel?")
                    .setPositiveButton("Igen", (dialog, which) -> {
                        Intent intent = new Intent(EventDetailsActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    })
                    .setNegativeButton("Nem", null)
                    .show();
        });
    }

    private void bindEvent() {
        titleText.setText(safe(event.getTitle()));
        headerText.setText(safe(event.getHeader()));
        dateText.setText(safe(event.getDate()));
        libraryText.setText(safe(event.getLibrary_name()));

        String description = safe(event.getDescription());
        if (description.isEmpty()) {
            description = "Nincs leírás ehhez az eseményhez.";
        }
        descriptionText.setText(description);

        String imageUrl = "http://10.0.2.2/szakdolgozat_api/assets/images/events/"
                + Uri.encode(event.getPicture() == null ? "" : event.getPicture());

        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.event_placeholder)
                .error(R.drawable.event_placeholder)
                .into(eventImage);
    }

    private void goBack() {
        if (fromAdmin) {
            Intent intent = new Intent(EventDetailsActivity.this, AdminDashboardActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(EventDetailsActivity.this, EventActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        }
        finish();
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }
}