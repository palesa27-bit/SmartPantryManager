package com.richfield.smartpantry.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.richfield.smartpantry.AddEditIngredientActivity;
import com.richfield.smartpantry.R;
import com.richfield.smartpantry.adapter.PantryAdapter;
import com.richfield.smartpantry.db.DatabaseHelper;
import com.richfield.smartpantry.model.PantryItem;

import java.util.List;

public class PantryFragment extends Fragment implements PantryAdapter.OnItemActionListener {

    private RecyclerView recyclerView;
    private LinearLayout emptyView;
    private TextView tvItemCount;
    private PantryAdapter adapter;
    private DatabaseHelper db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pantry, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = DatabaseHelper.getInstance(requireContext());

        recyclerView = view.findViewById(R.id.recycler_pantry);
        emptyView = view.findViewById(R.id.empty_view);
        tvItemCount = view.findViewById(R.id.tv_item_count);
        FloatingActionButton fab = view.findViewById(R.id.fab_add);

        adapter = new PantryAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        fab.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AddEditIngredientActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        List<PantryItem> items = db.getAllPantryItems();
        adapter.setItems(items);
        tvItemCount.setText(items.size() + " item" + (items.size() != 1 ? "s" : ""));
        emptyView.setVisibility(items.isEmpty() ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(items.isEmpty() ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onEdit(PantryItem item) {
        Intent intent = new Intent(requireContext(), AddEditIngredientActivity.class);
        intent.putExtra(AddEditIngredientActivity.EXTRA_ITEM_ID, item.getId());
        startActivity(intent);
    }

    @Override
    public void onDelete(PantryItem item) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Ingredient")
                .setMessage(getString(R.string.confirm_delete) + "\n\n" + item.getName())
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    db.deletePantryItem(item.getId());
                    Toast.makeText(requireContext(), R.string.item_deleted, Toast.LENGTH_SHORT).show();
                    loadData();
                })
                .setNegativeButton(R.string.no, null)
                .show();
    }
}
