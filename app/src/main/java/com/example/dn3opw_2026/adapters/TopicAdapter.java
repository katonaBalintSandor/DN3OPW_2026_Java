package com.example.dn3opw_2026.adapters;

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
import com.example.dn3opw_2026.model.Topic;
import java.util.List;

public class TopicAdapter extends RecyclerView.Adapter<TopicAdapter.ViewHolder> {

    public interface OnTopicClickListener {
        void onTopicClick(Topic topic);
    }

    private List<Topic> topics;
    private final OnTopicClickListener listener;

    public TopicAdapter(List<Topic> topics, OnTopicClickListener listener) {
        this.topics = topics;
        this.listener = listener;
    }

    public void updateData(List<Topic> newTopics) {
        this.topics = newTopics;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_topic, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Topic topic = topics.get(position);

        String bookTitle = topic.getBook_title() == null ? "" : topic.getBook_title();
        String rating = topic.getRating() + "/5";
        String userName =
                (topic.getUser_firstname() == null ? "" : topic.getUser_firstname()) + " " +
                        (topic.getUser_lastname() == null ? "" : topic.getUser_lastname());
        String topicTitle = topic.getTopic() == null ? "" : topic.getTopic();

        holder.titleRatingText.setText(bookTitle + " - " + rating);
        holder.userNameText.setText(userName.trim());
        holder.topicText.setText(topicTitle);

        String imageUrl = "http://10.0.2.2/szakdolgozat_api/assets/images/books/"
                + Uri.encode(topic.getBook_picture() == null ? "" : topic.getBook_picture());

        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.book_placeholder)
                .error(R.drawable.book_placeholder)
                .into(holder.bookImage);

        holder.openButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTopicClick(topic);
            }
        });
    }

    @Override
    public int getItemCount() {
        return topics != null ? topics.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleRatingText;
        TextView userNameText;
        TextView topicText;
        ImageView bookImage;
        Button openButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleRatingText = itemView.findViewById(R.id.titleRatingText);
            userNameText = itemView.findViewById(R.id.userNameText);
            topicText = itemView.findViewById(R.id.topicText);
            bookImage = itemView.findViewById(R.id.topicBookImage);
            openButton = itemView.findViewById(R.id.openTopicButton);
        }
    }
}