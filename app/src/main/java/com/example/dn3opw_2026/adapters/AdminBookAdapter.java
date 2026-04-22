package com.example.dn3opw_2026.adapters;

import android.content.Context;
import android.text.TextUtils;
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

public class AdminBookAdapter extends RecyclerView.Adapter<AdminBookAdapter.AdminBookViewHolder> {

    public interface OnAdminBookActionListener {
        void onEdit(Book book);
        void onQuantityEdit(Book book);
    }

    private final Context context;
    private final List<Book> books;
    private final OnAdminBookActionListener listener;

    private static final String IMAGE_BASE_URL = "http://10.0.2.2/szakdolgozat_api/assets/images/books/";

    public AdminBookAdapter(Context context, List<Book> books, OnAdminBookActionListener listener) {
        this.context = context;
        this.books = books;
        this.listener = listener;
    }

    public void updateData(List<Book> newBooks) {
        books.clear();
        books.addAll(newBooks);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AdminBookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_book, parent, false);
        return new AdminBookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminBookViewHolder holder, int position) {
        Book book = books.get(position);

        String title = book.getTitle() != null ? book.getTitle() : "";
        String author = book.getAuthor() != null ? book.getAuthor() : "";
        String category = book.getCategory() != null ? book.getCategory() : "";

        holder.bookTitleLine.setText(title + " - " + author);
        holder.bookMetaLine.setText("Kategória: " + category + " | Készlet: " + book.getQuantity());

        String picture = book.getPicture();
        String imageUrl;

        if (!TextUtils.isEmpty(picture)
                && (picture.startsWith("http://") || picture.startsWith("https://"))) {
            imageUrl = picture;
        } else if (!TextUtils.isEmpty(picture)) {
            imageUrl = IMAGE_BASE_URL + picture;
        } else {
            imageUrl = null;
        }

        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.book_placeholder)
                .error(R.drawable.book_placeholder)
                .into(holder.bookImage);

        holder.detailsButton.setOnClickListener(v -> listener.onEdit(book));
        holder.quantityButton.setOnClickListener(v -> listener.onQuantityEdit(book));
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    static class AdminBookViewHolder extends RecyclerView.ViewHolder {
        ImageView bookImage;
        TextView bookTitleLine;
        TextView bookMetaLine;
        Button detailsButton;
        Button quantityButton;

        public AdminBookViewHolder(@NonNull View itemView) {
            super(itemView);
            bookImage = itemView.findViewById(R.id.bookImage);
            bookTitleLine = itemView.findViewById(R.id.bookTitleLine);
            bookMetaLine = itemView.findViewById(R.id.bookMetaLine);
            detailsButton = itemView.findViewById(R.id.detailsButton);
            quantityButton = itemView.findViewById(R.id.quantityButton);
        }
    }
}