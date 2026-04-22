package com.example.dn3opw_2026.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.dn3opw_2026.R;
import com.example.dn3opw_2026.model.Book;
import java.util.List;

public class LeasedBookAdapter extends RecyclerView.Adapter<LeasedBookAdapter.ViewHolder> {

    public interface OnReturnClick {
        void onReturn(Book book);
    }

    private List<Book> books;
    private final OnReturnClick listener;

    public LeasedBookAdapter(List<Book> books, OnReturnClick listener) {
        this.books = books;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_leased_book, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Book b = books.get(position);

        String text =
                b.getTitle() + " — " + b.getAuthor() + "\n" +
                        "Könyvtár: " + b.getLibrary_name() + "\n" +
                        "Kölcsönzés: " + b.getLeased_date() + "\n" +
                        "Visszaadás: " + (b.getReturned_date() == null ? "—" : b.getReturned_date());

        holder.text.setText(text);

        if (b.getReturned_date() == null) {
            holder.button.setVisibility(View.VISIBLE);
            holder.button.setOnClickListener(v -> listener.onReturn(b));
        } else {
            holder.button.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView text;
        Button button;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            text = itemView.findViewById(R.id.bookText);
            button = itemView.findViewById(R.id.returnButton);
        }
    }
}