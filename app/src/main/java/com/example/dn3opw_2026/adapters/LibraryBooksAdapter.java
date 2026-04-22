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

public class LibraryBooksAdapter extends RecyclerView.Adapter<LibraryBooksAdapter.ViewHolder> {

    public interface OnBookClickListener {
        void onBookClick(Book book);
    }

    private List<Book> books;
    private final OnBookClickListener listener;

    public LibraryBooksAdapter(List<Book> books, OnBookClickListener listener) {
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
                .inflate(R.layout.item_library_book, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Book b = books.get(position);

        holder.bookTitleLine.setText(b.getTitle() + " - " + b.getAuthor());
        holder.bookMetaLine.setText("Kategória: " + b.getCategory() + " | Készlet: " + b.getQuantity());

        String imageUrl = "http://10.0.2.2/szakdolgozat_api/assets/images/books/"
                + Uri.encode(b.getPicture() == null ? "" : b.getPicture());

        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.book_placeholder)
                .error(R.drawable.book_placeholder)
                .into(holder.bookImage);

        holder.detailsButton.setOnClickListener(v -> listener.onBookClick(b));
    }

    @Override
    public int getItemCount() {
        return books == null ? 0 : books.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView bookImage;
        TextView bookTitleLine;
        TextView bookMetaLine;
        Button detailsButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            bookImage = itemView.findViewById(R.id.bookImage);
            bookTitleLine = itemView.findViewById(R.id.bookTitleLine);
            bookMetaLine = itemView.findViewById(R.id.bookMetaLine);
            detailsButton = itemView.findViewById(R.id.detailsButton);
        }
    }
}