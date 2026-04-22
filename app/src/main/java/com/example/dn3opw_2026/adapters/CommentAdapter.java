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

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {

    public interface OnDeleteCommentListener {
        void onDeleteComment(int commentId);
    }

    private final List<Comment> comments;
    private final int currentUserId;
    private final OnDeleteCommentListener listener;

    public CommentAdapter(List<Comment> comments, int currentUserId, OnDeleteCommentListener listener) {
        this.comments = comments;
        this.currentUserId = currentUserId;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Comment c = comments.get(position);

        String text = c.getUsername() + " • " + c.getCreated_at() + "\n\n" + c.getComment();
        holder.commentText.setText(text);

        if (c.getUser_id() == currentUserId) {
            holder.deleteButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setOnClickListener(v -> listener.onDeleteComment(c.getId()));
        } else {
            holder.deleteButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return comments != null ? comments.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView commentText;
        Button deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            commentText = itemView.findViewById(R.id.commentText);
            deleteButton = itemView.findViewById(R.id.deleteCommentButton);
        }
    }
}