package com.richfield.smartpantry.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.richfield.smartpantry.R;
import com.richfield.smartpantry.RecipeDetailActivity;
import com.richfield.smartpantry.adapter.RecipeAdapter;
import com.richfield.smartpantry.db.DatabaseHelper;
import com.richfield.smartpantry.model.Recipe;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SuggestedFragment extends Fragment implements RecipeAdapter.OnRecipeClickListener {

    private RecyclerView recyclerView;
    private LinearLayout emptyView;
    private RecipeAdapter adapter;
    private DatabaseHelper db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_suggested, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = DatabaseHelper.getInstance(requireContext());

        recyclerView = view.findViewById(R.id.recycler_suggested);
        emptyView = view.findViewById(R.id.empty_view);

        adapter = new RecipeAdapter(this, true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        List<Recipe> suggested = db.getSuggestedRecipes();
        Set<Long> ids = new HashSet<>();
        for (Recipe r : suggested) {
            ids.add(r.getId());
        }
        adapter.setRecipes(suggested);
        adapter.setCanMakeIds(ids);

        emptyView.setVisibility(suggested.isEmpty() ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(suggested.isEmpty() ? View.GONE : View.VISIBLE);
    }

    @Override
    public void onRecipeClick(Recipe recipe) {
        Intent intent = new Intent(requireContext(), RecipeDetailActivity.class);
        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.getId());
        startActivity(intent);
    }
}
