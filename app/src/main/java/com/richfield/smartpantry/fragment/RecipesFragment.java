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

/**
 * Screen that lists all recipes in a RecyclerView and marks which ones
 * the user can make with the ingredients currently in the pantry.
 */
public class RecipesFragment extends Fragment implements RecipeAdapter.OnRecipeClickListener {

    private RecyclerView recyclerView;
    private TextView tvCount;
    private RecipeAdapter adapter;
    private DatabaseHelper db;

    /** Inflates the fragment's layout (fragment_recipes.xml). */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_recipes, container, false);
    }

    /** Finds the views and sets up the RecyclerView with its adapter, once the layout exists. */
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

    /**
     * Reloads the data every time the screen becomes visible, so changes made
     * on the pantry screen are reflected here straight away.
     */
    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    /**
     * Reads recipes from SQLite and works out which ones can be made.
     * getSuggestedRecipes() applies the strict rule: every ingredient must be
     * in the pantry in sufficient quantity. Their IDs are collected in a Set
     * so the adapter can quickly check whether to mark each recipe as makeable.
     */
    private void loadData() {
        List<Recipe> recipes = db.getAllRecipes();
        List<Recipe> suggested = db.getSuggestedRecipes();
        Set<Long> canMakeIds = new HashSet<>();
        for (Recipe r : suggested) {
            canMakeIds.add(r.getId());
        }
        adapter.setRecipes(recipes);
        adapter.setCanMakeIds(canMakeIds);
        // Handles singular and plural: "1 recipe", "18 recipes".
        tvCount.setText(recipes.size() + " recipe" + (recipes.size() != 1 ? "s" : ""));
    }

    /** Opens the detail screen for the tapped recipe, passing its ID through an Intent extra. */
    @Override
    public void onRecipeClick(Recipe recipe) {
        Intent intent = new Intent(requireContext(), RecipeDetailActivity.class);
        intent.putExtra(RecipeDetailActivity.EXTRA_RECIPE_ID, recipe.getId());
        startActivity(intent);
    }
}