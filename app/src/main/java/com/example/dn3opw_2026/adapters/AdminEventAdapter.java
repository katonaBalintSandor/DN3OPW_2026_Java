package com.example.dn3opw_2026.adapters;

import android.content.Context;
import android.net.Uri;
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
import com.example.dn3opw_2026.model.Event;
import java.util.List;

public class AdminEventAdapter extends RecyclerView.Adapter<AdminEventAdapter.EventViewHolder> {

    public interface OnAdminEventActionListener {
        void onOpen(Event event);
        void onEdit(Event event);
        void onDelete(Event event);
    }
    private final Context context;
    private final List<Event> events;
    private final int adminId;
    private final OnAdminEventActionListener listener;

    private static final String IMAGE_BASE_URL = "http://10.0.2.2/szakdolgozat_api/assets/images/events/";

    public AdminEventAdapter(Context context, List<Event> events, int adminId, OnAdminEventActionListener listener) {
        this.context = context;
        this.events = events;
        this.adminId = adminId;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin_event, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        Event event = events.get(position);

        String title = event.getTitle() != null ? event.getTitle() : "";
        String header = event.getHeader() != null ? event.getHeader() : "";
        String libraryName = event.getLibrary_name() != null ? event.getLibrary_name() : "Ismeretlen könyvtár";
        String date = event.getDate() != null ? event.getDate() : "";

        holder.titleText.setText(title);
        holder.headerText.setText(header);
        holder.metaText.setText(libraryName + " - " + date);

        String picture = event.getPicture();
        String imageUrl;

        if (!TextUtils.isEmpty(picture)
                && (picture.startsWith("http://") || picture.startsWith("https://"))) {
            imageUrl = picture;
        } else if (!TextUtils.isEmpty(picture)) {
            imageUrl = IMAGE_BASE_URL + Uri.encode(picture);
        } else {
            imageUrl = null;
        }

        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.event_placeholder)
                .error(R.drawable.event_placeholder)
                .into(holder.eventImage);

        holder.openButton.setOnClickListener(v -> listener.onOpen(event));

        boolean isOwner = event.getAdmin_id() == adminId;
        holder.editButton.setVisibility(isOwner ? View.VISIBLE : View.GONE);
        holder.deleteButton.setVisibility(isOwner ? View.VISIBLE : View.GONE);

        holder.editButton.setOnClickListener(v -> listener.onEdit(event));
        holder.deleteButton.setOnClickListener(v -> listener.onDelete(event));
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        TextView titleText;
        TextView headerText;
        TextView metaText;
        ImageView eventImage;
        Button openButton;
        Button editButton;
        Button deleteButton;

        public EventViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.titleText);
            headerText = itemView.findViewById(R.id.headerText);
            metaText = itemView.findViewById(R.id.metaText);
            eventImage = itemView.findViewById(R.id.eventImage);
            openButton = itemView.findViewById(R.id.openButton);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}