package com.richfield.smartpantry.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

public class RecipesFragment extends Fragment implements RecipeAdapter.OnRecipeClickListener {

    private RecyclerView recyclerView;
    private TextView tvCount;
    private RecipeAdapter adapter;
    private DatabaseHelper db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recipes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = DatabaseHelper.getInstance(requireContext());

        recyclerView = view.findViewById(R.id.recycler_recipes);
        tvCount = view.findViewById(R.id.tv_recipe_count);

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
        List<Recipe> recipes = db.getAllRecipes();
        List<Recipe> suggested = db.getSuggestedRecipes();
        Set<Long> canMakeIds = new HashSet<>();
        for (Recipe r : suggested) {
            canMakeIds.add(r.getId());
        }
        adapter.setRecipes(recipes);
        adapter.setCanMakeIds(canMakeIds);
        tvCount.setText(recipes.size() + " recipe" + (recipes.size() != 1 ? "s" : ""));
    }

    @Override
    public void onRecipeClick(Recipe recipe) {
        Intent intent = new Intent(requireContext(), RecipeDetailActivity.class);
        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.getId());
        startActivity(intent);
    }
}
