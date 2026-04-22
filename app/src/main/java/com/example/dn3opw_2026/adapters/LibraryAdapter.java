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
import com.example.dn3opw_2026.model.Library;
import java.util.List;

public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.ViewHolder> {

    public interface OnLibraryClickListener {
        void onLibraryClick(Library library);
    }

    private List<Library> libraries;
    private final OnLibraryClickListener listener;

    public LibraryAdapter(List<Library> libraries, OnLibraryClickListener listener) {
        this.libraries = libraries;
        this.listener = listener;
    }

    public void updateData(List<Library> newLibraries) {
        this.libraries = newLibraries;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_library, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Library lib = libraries.get(position);

        holder.title.setText(lib.getName() + " - " + lib.getCity());
        holder.quantity.setText("Könyvek száma: " + lib.getTotal_books());

        String picture = lib.getPicture();
        String imageUrl = "http://10.0.2.2/szakdolgozat_api/assets/images/libraries/"
                + Uri.encode(picture == null ? "" : picture);

        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.library_placeholder)
                .error(R.drawable.library_placeholder)
                .into(holder.libraryImage);

        holder.openButton.setOnClickListener(v -> listener.onLibraryClick(lib));
    }

    @Override
    public int getItemCount() {
        return libraries != null ? libraries.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView quantity;
        Button openButton;
        ImageView libraryImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.libraryTitle);
            quantity = itemView.findViewById(R.id.libraryQuantity);
            openButton = itemView.findViewById(R.id.openLibraryButton);
            libraryImage = itemView.findViewById(R.id.libraryImage);
        }
    }
}