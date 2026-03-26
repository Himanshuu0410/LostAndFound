package com.example.lostandfound.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lostandfound.R;
import com.example.lostandfound.model.ItemModel;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {

    private final Context        context;
    private final List<ItemModel> items;
    private       OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(ItemModel item);
    }

    public ItemAdapter(Context context, List<ItemModel> items) {
        this.context = context;
        this.items   = items;
    }

    public void setOnItemClickListener(OnItemClickListener l) { this.listener = l; }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        ItemModel item = items.get(position);

        h.tvItemName.setText(item.getItemName());
        h.tvCategory.setText(item.getCategory());
        h.tvLocation.setText("📍 " + item.getAddress());
        h.tvDate.setText(item.getDate());
        h.tvReporter.setText("👤 " + item.getPersonName());

        // Type badge
        if (item.isLost()) {
            h.tvBadge.setText("LOST");
            h.tvBadge.setBackgroundResource(R.drawable.badge_lost);
            h.tvBadge.setTextColor(0xFFFF4D6D);
            h.cardView.setCardBackgroundColor(0xFF181C33);
        } else {
            h.tvBadge.setText("FOUND");
            h.tvBadge.setBackgroundResource(R.drawable.badge_found);
            h.tvBadge.setTextColor(0xFF00C896);
            h.cardView.setCardBackgroundColor(0xFF181C33);
        }

        // Status chip
        if ("resolved".equals(item.getStatus())) {
            h.tvStatus.setVisibility(View.VISIBLE);
            h.tvStatus.setText("✓ Resolved");
        } else {
            h.tvStatus.setVisibility(View.GONE);
        }

        // Image
        if (item.getImageUri() != null && !item.getImageUri().isEmpty()) {
            try {
                h.imgItem.setImageURI(Uri.parse(item.getImageUri()));
                h.imgItem.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                h.imgItem.setVisibility(View.GONE);
            }
        } else {
            h.imgItem.setVisibility(View.GONE);
        }

        h.cardView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(item);
        });
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView  cardView;
        ImageView imgItem;
        TextView  tvItemName, tvCategory, tvLocation, tvDate, tvReporter, tvBadge, tvStatus;

        ViewHolder(View v) {
            super(v);
            cardView    = v.findViewById(R.id.cardItem);
            imgItem     = v.findViewById(R.id.imgItem);
            tvItemName  = v.findViewById(R.id.tvItemName);
            tvCategory  = v.findViewById(R.id.tvCategory);
            tvLocation  = v.findViewById(R.id.tvLocation);
            tvDate      = v.findViewById(R.id.tvDate);
            tvReporter  = v.findViewById(R.id.tvReporter);
            tvBadge     = v.findViewById(R.id.tvBadge);
            tvStatus    = v.findViewById(R.id.tvStatus);
        }
    }
}
