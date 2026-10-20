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

public class PantryAdapter extends RecyclerView.Adapter<PantryAdapter.ViewHolder> {

    public interface OnItemActionListener {
        void onEdit(PantryItem item);
        void onDelete(PantryItem item);
    }

    private List<PantryItem> items = new ArrayList<>();
    private final OnItemActionListener listener;

    public PantryAdapter(OnItemActionListener listener) {
        this.listener = listener;
    }

    public void setItems(List<PantryItem> items) {
        this.items = items != null ? items : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pantry, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PantryItem item = items.get(position);
        holder.tvName.setText(item.getName());

        String qtyText;
        if (item.getQuantity() == (long) item.getQuantity()) {
            qtyText = (long) item.getQuantity() + " " + item.getUnit();
        } else {
            qtyText = item.getQuantity() + " " + item.getUnit();
        }
        holder.tvQuantity.setText(qtyText);

        if (item.getExpiryDate() != null && !item.getExpiryDate().isEmpty()) {
            holder.tvExpiry.setVisibility(View.VISIBLE);
            holder.tvExpiry.setText("Expires: " + item.getExpiryDate());
        } else {
            holder.tvExpiry.setVisibility(View.GONE);
        }

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEdit(item);
        });
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(item);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

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
