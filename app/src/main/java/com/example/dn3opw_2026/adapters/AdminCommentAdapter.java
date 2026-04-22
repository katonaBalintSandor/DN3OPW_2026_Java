package com.example.dn3opw_2026.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.dn3opw_2026.R;
import com.example.dn3opw_2026.model.Comment;

import java.util.List;

public class AdminCommentAdapter extends RecyclerView.Adapter<AdminCommentAdapter.AdminCommentViewHolder> {

    public interface OnDeleteCommentListener {
        void onDelete(int commentId);
    }

    private final List<Comment> comments;
    private final OnDeleteCommentListener listener;

    public AdminCommentAdapter(List<Comment> comments, OnDeleteCommentListener listener) {
        this.comments = comments;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AdminCommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin_comment, parent, false);
        return new AdminCommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminCommentViewHolder holder, int position) {
        Comment comment = comments.get(position);

        String fullName = (safe(comment.getFirstname()) + " " + safe(comment.getLastname())).trim();
        if (fullName.isEmpty()) {
            fullName = "Ismeretlen felhasználó";
        }

        holder.usernameText.setText(fullName);
        holder.commentText.setText(safe(comment.getComment()));

        holder.deleteButton.setOnClickListener(v -> listener.onDelete(comment.getId()));
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }

    static class AdminCommentViewHolder extends RecyclerView.ViewHolder {
        TextView usernameText;
        TextView commentText;
        Button deleteButton;

        public AdminCommentViewHolder(@NonNull View itemView) {
            super(itemView);
            usernameText = itemView.findViewById(R.id.usernameText);
            commentText = itemView.findViewById(R.id.commentText);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}