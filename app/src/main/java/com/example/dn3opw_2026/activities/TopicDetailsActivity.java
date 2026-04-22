package com.example.dn3opw_2026.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
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
import com.bumptech.glide.Glide;
import com.example.dn3opw_2026.R;
import com.example.dn3opw_2026.adapters.CommentAdapter;
import com.example.dn3opw_2026.model.Book;
import com.example.dn3opw_2026.model.Comment;
import com.example.dn3opw_2026.model.Topic;
import com.example.dn3opw_2026.model.User;
import com.example.dn3opw_2026.network.responses.BaseResponse;
import com.example.dn3opw_2026.network.responses.BookDetailResponse;
import com.example.dn3opw_2026.network.responses.CommentsResponse;
import com.example.dn3opw_2026.network.responses.TopicDetailResponse;
import com.example.dn3opw_2026.repository.BookRepository;
import com.example.dn3opw_2026.repository.CommentRepository;
import com.example.dn3opw_2026.repository.TopicRepository;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TopicDetailsActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageButton menuButton;
    private TextView titleHeaderText;
    private Button deleteTopicButton;
    private Button backButton;
    private Button profileButton, communityButton, eventsButton, logoutButton;
    private ImageView bgImage;
    private ImageView bookImage;
    private TextView topicDetailsText;
    private Button addCommentButton;
    private RecyclerView commentRecycler;
    private TextView pageLabel;
    private Button prevButton;
    private Button nextButton;
    private Spinner pageSizeSpinner;

    private Topic topic;
    private User user;
    private Book book;
    private int topicId;
    private BookRepository bookRepository;
    private TopicRepository topicRepository;
    private CommentRepository commentRepository;
    private CommentAdapter commentAdapter;
    private final List<Comment> allComments = new ArrayList<>();
    private final List<Comment> pageComments = new ArrayList<>();

    private int pageSize = 10;
    private int currentPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_details);

        user = (User) getIntent().getSerializableExtra("user");
        topicId = getIntent().getIntExtra("topic_id", 0);

        if (user == null || topicId <= 0) {
            finish();
            return;
        }

        bookRepository = new BookRepository();
        topicRepository = new TopicRepository();
        commentRepository = new CommentRepository();

        initViews();
        setupRecycler();
        setupSpinner();
        setupListeners();
        bindHeader();
        loadTopic();
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

        addCommentButton = findViewById(R.id.addCommentButton);
        commentRecycler = findViewById(R.id.commentRecycler);
        pageLabel = findViewById(R.id.pageLabel);
        prevButton = findViewById(R.id.prevButton);
        nextButton = findViewById(R.id.nextButton);
        pageSizeSpinner = findViewById(R.id.pageSizeSpinner);
    }

    private void setupRecycler() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        commentRecycler.setLayoutManager(layoutManager);
        commentRecycler.setNestedScrollingEnabled(false);
        commentRecycler.setHasFixedSize(false);

        commentAdapter = new CommentAdapter(pageComments, user.getId(), this::deleteOwnComment);
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
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
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

        addCommentButton.setOnClickListener(v -> addCommentPopup());

        prevButton.setOnClickListener(v -> prevPage());
        nextButton.setOnClickListener(v -> nextPage());

        deleteTopicButton.setOnClickListener(v -> deleteTopicPopup());

        profileButton.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            Intent intent = new Intent(TopicDetailsActivity.this, ProfileActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        });

        communityButton.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            goBack();
        });

        eventsButton.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            Intent intent = new Intent(TopicDetailsActivity.this, EventActivity.class);
            intent.putExtra("user", user);
            startActivity(intent);
        });

        logoutButton.setOnClickListener(v -> {
            drawerLayout.closeDrawer(GravityCompat.START);
            new AlertDialog.Builder(this)
                    .setTitle("Kijelentkezés")
                    .setMessage("Biztosan kijelentkezel?")
                    .setPositiveButton("Igen", (dialog, which) -> {
                        Intent intent = new Intent(TopicDetailsActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    })
                    .setNegativeButton("Nem", null)
                    .show();
        });
    }

    private void bindHeader() {
        titleHeaderText.setText("Téma részletek");
    }

    private void loadTopic() {
        topicRepository.getTopicById(topicId).enqueue(new Callback<TopicDetailResponse>() {
            @Override
            public void onResponse(Call<TopicDetailResponse> call, Response<TopicDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    topic = response.body().getTopic();

                    if (topic != null) {
                        deleteTopicButton.setVisibility(topic.getUser_id() == user.getId() ? View.VISIBLE : View.GONE);
                        loadBook();
                        loadComments();
                    } else {
                        showMessage("Hiba", "Nem sikerült betölteni a témát.");
                    }
                } else {
                    showMessage("Hiba", "Nem sikerült betölteni a témát.");
                }
            }

            @Override
            public void onFailure(Call<TopicDetailResponse> call, Throwable t) {
                showMessage("Hiba", "Hálózati hiba.");
            }
        });
    }

    private void loadBook() {
        if (topic == null) return;

        bookRepository.getBookDetails(topic.getBook_id(), topic.getLibrary_id()).enqueue(new Callback<BookDetailResponse>() {
            @Override
            public void onResponse(Call<BookDetailResponse> call, Response<BookDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    book = response.body().getBook();
                } else {
                    book = null;
                }
                bindLeftPanel();
            }

            @Override
            public void onFailure(Call<BookDetailResponse> call, Throwable t) {
                book = null;
                bindLeftPanel();
            }
        });
    }

    private void bindLeftPanel() {
        String pictureName = null;

        if (book != null && book.getPicture() != null && !book.getPicture().isEmpty()) {
            pictureName = book.getPicture();
        } else if (topic != null && topic.getBook_picture() != null) {
            pictureName = topic.getBook_picture();
        }

        String imageUrl = "http://10.0.2.2/szakdolgozat_api/assets/images/books/"
                + Uri.encode(pictureName == null ? "" : pictureName);

        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.book_placeholder)
                .error(R.drawable.book_placeholder)
                .into(bookImage);

        String bookTitle = (book != null) ? safe(book.getTitle()) : safe(topic.getBook_title());
        String author = (book != null) ? safe(book.getAuthor()) : "";
        String username = safe(topic.getUsername());

        String details =
                "Könyv:\n" + bookTitle + (author.isEmpty() ? "" : " — " + author) + "\n\n" +
                        "Értékelés:\n" + topic.getRating() + "/5\n\n" +
                        "Szerző:\n" + username + "\n\n" +
                        "Téma:\n" + safe(topic.getTopic());

        topicDetailsText.setText(details);
    }

    private void loadComments() {
        commentRepository.getCommentsForTopic(topicId).enqueue(new Callback<CommentsResponse>() {
            @Override
            public void onResponse(Call<CommentsResponse> call, Response<CommentsResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    allComments.clear();
                    if (response.body().getComments() != null) {
                        allComments.addAll(response.body().getComments());
                    }
                } else {
                    allComments.clear();
                }

                currentPage = 1;
                renderComments();
            }

            @Override
            public void onFailure(Call<CommentsResponse> call, Throwable t) {
                allComments.clear();
                currentPage = 1;
                renderComments();
            }
        });
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
        topicRepository.deleteTopic(topic.getId()).enqueue(new Callback<BaseResponse>() {
            @Override
            public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    showMessage("Siker", "A téma és a hozzászólások törölve lettek!");
                    goBack();
                } else {
                    showMessage("Hiba", "Nem sikerült törölni a témát.");
                }
            }

            @Override
            public void onFailure(Call<BaseResponse> call, Throwable t) {
                showMessage("Hiba", "Nem sikerült törölni a témát.");
            }
        });
    }

    private void deleteOwnComment(int commentId) {
        new AlertDialog.Builder(this)
                .setTitle("Megerősítés")
                .setMessage("Biztosan törölni szeretnéd ezt a hozzászólást?")
                .setNegativeButton("Nem", null)
                .setPositiveButton("Igen", (dialog, which) -> {
                    commentRepository.deleteComment(commentId, user.getId()).enqueue(new Callback<BaseResponse>() {
                        @Override
                        public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                            if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                                showMessage("Siker", "A hozzászólás törölve lett.");
                                loadComments();
                            } else {
                                showMessage("Hiba", "Nem sikerült törölni a hozzászólást.");
                            }
                        }

                        @Override
                        public void onFailure(Call<BaseResponse> call, Throwable t) {
                            showMessage("Hiba", "Nem sikerült törölni a hozzászólást.");
                        }
                    });
                })
                .show();
    }

    private void addCommentPopup() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_comment, null);
        EditText commentInput = dialogView.findViewById(R.id.commentInput);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Új hozzászólás")
                .setView(dialogView)
                .setNegativeButton("Mégse", null)
                .setPositiveButton("Mentés", null)
                .create();

        dialog.show();

        Button saveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        saveButton.setOnClickListener(v -> {
            String text = String.valueOf(commentInput.getText()).trim();

            if (text.isEmpty()) {
                showMessage("Hiba", "A hozzászólás nem lehet üres!");
                return;
            }

            commentRepository.addComment(topic.getId(), user.getId(), text).enqueue(new Callback<BaseResponse>() {
                @Override
                public void onResponse(Call<BaseResponse> call, Response<BaseResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        dialog.dismiss();

                        commentRepository.getCommentsForTopic(topic.getId()).enqueue(new Callback<CommentsResponse>() {
                            @Override
                            public void onResponse(Call<CommentsResponse> call, Response<CommentsResponse> response) {
                                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                                    allComments.clear();
                                    if (response.body().getComments() != null) {
                                        allComments.addAll(response.body().getComments());
                                    }

                                    int totalPages = Math.max(1, (allComments.size() + pageSize - 1) / pageSize);
                                    currentPage = totalPages;
                                    renderComments();
                                }
                            }

                            @Override
                            public void onFailure(Call<CommentsResponse> call, Throwable t) {
                                showMessage("Hiba", "Nem sikerült frissíteni a hozzászólásokat.");
                            }
                        });

                    } else {
                        showMessage("Hiba", "Nem sikerült menteni a hozzászólást.");
                    }
                }

                @Override
                public void onFailure(Call<BaseResponse> call, Throwable t) {
                    showMessage("Hiba", "Nem sikerült menteni a hozzászólást.");
                }
            });
        });
    }

    private void goBack() {
        Intent intent = new Intent(this, CommunityActivity.class);
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

    private String safe(String s) {
        return s == null ? "" : s;
    }
}