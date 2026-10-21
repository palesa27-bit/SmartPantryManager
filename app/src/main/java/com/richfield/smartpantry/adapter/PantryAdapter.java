package com.richfield.smartpantry.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.richfield.smartpantry.R;
import com.richfield.smartpantry.model.PantryItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Custom RecyclerView adapter that shows each pantry item as a card.
 * It connects the list of PantryItem objects to the item_pantry.xml layout.
 */
public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.ViewHolder> {

    /** Lets the Activity decide what happens when the edit or delete button is pressed. */
    public interface OnItemActionListener {
        void onEdit(PantryItem item);
        void onDelete(PantryItem item);
    }

    private List<PantryItem> items = new ArrayList<>();
    private final OnItemActionListener listener;

    public PantryAdapter(OnItemActionListener listener) {
        this.listener = listener;
    }

    /** Replaces the list with fresh data from the database and redraws the screen. */
    public void setItems(List<PantryItem> items) {
        this.items = items != null ? items : new ArrayList<>();
        notifyDataSetChanged();
    }

    /**
     * Inflates the card layout (item_pantry.xml) into a View.
     * This only runs a few times, because RecyclerView reuses cards
     * as they scroll off screen instead of creating new ones.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Fills one card with the data of one pantry item.
     * Runs every time a card scrolls into view.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PantryItem item = items.get(position);
        holder.tvName.setText(item.getName());

        // Show whole numbers without a decimal point (2 instead of 2.0).
        String qtyText;
        if (item.getQuantity() == (long) item.getQuantity()) {
            qtyText = (long) item.getQuantity() + " " + item.getUnit();
        } else {
            qtyText = item.getQuantity() + " " + item.getUnit();
        }
        holder.tvQuantity.setText(qtyText);

        // Only show the expiry line when the item actually has an expiry date.
        if (item.getExpiryDate() != null && !item.getExpiryDate().isEmpty()) {
            holder.tvExpiry.setVisibility(View.VISIBLE);
            holder.tvExpiry.setText("Expires: " + item.getExpiryDate());
        } else {
            holder.tvExpiry.setVisibility(View.GONE);
        }

        // Pass button presses back to the Activity through the listener.
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEdit(item);
        });
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(item);
        });
    }

    /** RecyclerView asks this to know how many cards to prepare. */
    @Override
    public int getItemCount() {
        return items.size();
    }

    /** Holds references to the views inside one card so they are looked up only once. */
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvQuantity, tvExpiry;
        ImageButton btnEdit, btnDelete;

        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            tvExpiry = itemView.findViewById(R.id.tv_expiry);
            btnEdit = itemView.findViewById(R.id.btn_edit);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}