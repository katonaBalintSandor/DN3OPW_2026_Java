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
import com.example.dn3opw_2026.model.Event;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.ViewHolder> {

    public interface OnEventClickListener {
        void onEventClick(Event event);
    }

    private final List<Event> events;
    private final OnEventClickListener listener;

    public EventAdapter(List<Event> events, OnEventClickListener listener) {
        this.events = events;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_event, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Event event = events.get(position);

        String title = event.getTitle() == null ? "" : event.getTitle();
        String date = event.getDate() == null ? "" : event.getDate();
        String libraryName = event.getLibrary_name() == null ? "" : event.getLibrary_name();

        holder.eventTitleDate.setText(title + " - " + date);
        holder.eventLibraryName.setText(libraryName);

        String imageUrl = "http://10.0.2.2/szakdolgozat_api/assets/images/events/"
                + Uri.encode(event.getPicture() == null ? "" : event.getPicture());

        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .placeholder(R.drawable.event_placeholder)
                .error(R.drawable.event_placeholder)
                .into(holder.eventImage);

        holder.detailsButton.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEventClick(event);
            }
        });
    }

    @Override
    public int getItemCount() {
        return events != null ? events.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView eventImage;
        TextView eventTitleDate;
        TextView eventLibraryName;
        Button detailsButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            eventImage = itemView.findViewById(R.id.eventImage);
            eventTitleDate = itemView.findViewById(R.id.eventTitleDate);
            eventLibraryName = itemView.findViewById(R.id.eventLibraryName);
            detailsButton = itemView.findViewById(R.id.detailsButton);
        }
    }
}