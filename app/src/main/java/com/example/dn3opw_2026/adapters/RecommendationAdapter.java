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
import com.example.dn3opw_2026.model.Book;
import java.util.List;

public class RecommendationAdapter extends RecyclerView.Adapter<RecommendationAdapter.ViewHolder> {

    public interface OnRecommendationClickListener {
        void onRecommendationClick(Book book);
    }

    private List<Book> books;
    private final OnRecommendationClickListener listener;

    public RecommendationAdapter(List<Book> books, OnRecommendationClickListener listener) {
        this.books = books;
        this.listener = listener;
    }

    public void updateData(List<Book> newBooks) {
        this.books = newBooks;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recommendation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Book book = books.get(position);

        holder.title.setText(book.getTitle() + " - " + book.getAuthor());
        holder.info.setText("Kategória: " + book.getCategory() + " | Készlet: " + book.getQuantity());

        String picture = book.getPicture();
        String imageUrl = "http://10.0.2.2/szakdolgozat_api/assets/images/books/"
                + Uri.encode(picture == null ? "" : picture);

        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.book_placeholder)
                .error(R.drawable.book_placeholder)
                .into(holder.bookImage);

        holder.openButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRecommendationClick(book);
            }
        });
    }

    @Override
    public int getItemCount() {
        return books != null ? books.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView info;
        Button openButton;
        ImageView bookImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.bookTitle);
            info = itemView.findViewById(R.id.bookInfo);
            openButton = itemView.findViewById(R.id.openBookButton);
            bookImage = itemView.findViewById(R.id.bookImage);
        }
    }
}