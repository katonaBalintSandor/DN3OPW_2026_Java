package com.example.dn3opw_2026.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.dn3opw_2026.R;
import com.example.dn3opw_2026.model.AdminCommunityTopic;

import java.util.List;

public class AdminCommunityAdapter extends RecyclerView.Adapter<AdminCommunityAdapter.AdminCommunityViewHolder> {

    public interface OnTopicClickListener {
        void onOpen(AdminCommunityTopic topic);
    }

    private final Context context;
    private final List<AdminCommunityTopic> topics;
    private final OnTopicClickListener listener;

    public AdminCommunityAdapter(Context context, List<AdminCommunityTopic> topics, OnTopicClickListener listener) {
        this.context = context;
        this.topics = topics;
        this.listener = listener;
    }

    public void updateData(List<AdminCommunityTopic> newTopics) {
        topics.clear();
        topics.addAll(newTopics);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AdminCommunityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_community_topic, parent, false);
        return new AdminCommunityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminCommunityViewHolder holder, int position) {
        AdminCommunityTopic topic = topics.get(position);

        holder.titleRatingText.setText(
                safe(topic.getBook_title()) + " - " + topic.getRating() + "/5"
        );

        String fullName = (safe(topic.getUser_firstname()) + " " + safe(topic.getUser_lastname())).trim();
        holder.userTopicText.setText(
                fullName + " - " + safe(topic.getTopic())
        );

        String imageUrl = "http://10.0.2.2/szakdolgozat_api/assets/images/books/" +
                Uri.encode(topic.getBook_picture() == null ? "" : topic.getBook_picture());

        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.book_placeholder)
                .error(R.drawable.book_placeholder)
                .into(holder.bookImage);

        holder.openButton.setOnClickListener(v -> listener.onOpen(topic));
    }

    @Override
    public int getItemCount() {
        return topics.size();
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }

    static class AdminCommunityViewHolder extends RecyclerView.ViewHolder {
        ImageView bookImage;
        TextView titleRatingText;
        TextView userTopicText;
        Button openButton;

        public AdminCommunityViewHolder(@NonNull View itemView) {
            super(itemView);
            bookImage = itemView.findViewById(R.id.bookImage);
            titleRatingText = itemView.findViewById(R.id.titleRatingText);
            userTopicText = itemView.findViewById(R.id.userTopicText);
            openButton = itemView.findViewById(R.id.openButton);
        }
    }
}