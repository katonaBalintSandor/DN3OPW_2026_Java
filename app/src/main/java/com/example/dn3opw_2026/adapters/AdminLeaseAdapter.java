package com.example.dn3opw_2026.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
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
import com.example.dn3opw_2026.model.AdminLease;

import java.util.List;

public class AdminLeaseAdapter extends RecyclerView.Adapter<AdminLeaseAdapter.AdminLeaseViewHolder> {

    public interface OnLeaseActionListener {
        void onReturn(AdminLease lease);
    }

    private final Context context;
    private final List<AdminLease> leases;
    private final OnLeaseActionListener listener;

    public AdminLeaseAdapter(Context context, List<AdminLease> leases, OnLeaseActionListener listener) {
        this.context = context;
        this.leases = leases;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AdminLeaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_lease, parent, false);
        return new AdminLeaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminLeaseViewHolder holder, int position) {
        AdminLease lease = leases.get(position);

        holder.titleCategoryText.setText(
                safe(lease.getTitle()) + " - " + safe(lease.getCategory())
        );
        holder.userText.setText("Kölcsönözte: " + safe(lease.getUsername()));
        holder.dateText.setText("Dátum: " + safe(lease.getLeased_date()));

        String imageUrl = "http://10.0.2.2/szakdolgozat_api/assets/images/books/" +
                Uri.encode(lease.getPicture() == null ? "" : lease.getPicture());

        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.book_placeholder)
                .error(R.drawable.book_placeholder)
                .into(holder.bookImage);

        if (lease.isActiveLease()) {
            holder.returnButton.setEnabled(true);
            holder.returnButton.setText("Visszavétel");
            holder.returnButton.setOnClickListener(v -> listener.onReturn(lease));
            holder.returnButton.setBackgroundTintList(ColorStateList.valueOf(0xFF5CB85C));
        } else {
            holder.returnButton.setEnabled(false);
            holder.returnButton.setText("Már visszavéve");
            holder.returnButton.setOnClickListener(null);
            holder.returnButton.setBackgroundTintList(ColorStateList.valueOf(0xFF9E9E9E));
        }
    }

    @Override
    public int getItemCount() {
        return leases.size();
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }

    static class AdminLeaseViewHolder extends RecyclerView.ViewHolder {
        TextView titleCategoryText;
        TextView userText;
        TextView dateText;
        ImageView bookImage;
        Button returnButton;

        public AdminLeaseViewHolder(@NonNull View itemView) {
            super(itemView);
            titleCategoryText = itemView.findViewById(R.id.titleCategoryText);
            userText = itemView.findViewById(R.id.userText);
            dateText = itemView.findViewById(R.id.dateText);
            bookImage = itemView.findViewById(R.id.bookImage);
            returnButton = itemView.findViewById(R.id.returnButton);
        }
    }
}