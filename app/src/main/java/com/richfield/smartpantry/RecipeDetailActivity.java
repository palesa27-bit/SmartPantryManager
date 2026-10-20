package com.richfield.smartpantry;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.richfield.smartpantry.db.DatabaseHelper;
import com.richfield.smartpantry.model.Recipe;
import com.richfield.smartpantry.model.RecipeIngredient;

public class RecipeDetailActivity extends AppCompatActivity {

    public static final String EXTRA_RECIPE_ID = "recipe_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
        toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material);
        toolbar.setTitle("Recipe Details");

        long recipeId = getIntent().getLongExtra(EXTRA_RECIPE_ID, -1);
        if (recipeId == -1) {
            finish();
            return;
        }

        DatabaseHelper db = DatabaseHelper.getInstance(this);
        Recipe recipe = db.getRecipe(recipeId);
        if (recipe == null) {
            finish();
            return;
        }

        TextView tvName = findViewById(R.id.tv_recipe_name);
        TextView tvIngredients = findViewById(R.id.tv_ingredients);
        TextView tvMethod = findViewById(R.id.tv_method);
        TextView tvCanMake = findViewById(R.id.tv_can_make_badge);

        tvName.setText(recipe.getName());
        tvMethod.setText(recipe.getMethod());

        StringBuilder ingBuilder = new StringBuilder();
        if (recipe.getIngredients() != null) {
            for (RecipeIngredient ing : recipe.getIngredients()) {
                ingBuilder.append("• ").append(ing.getDisplay()).append("\n");
            }
        }
        tvIngredients.setText(ingBuilder.toString().trim());

        if (db.canMakeRecipe(recipeId)) {
            tvCanMake.setVisibility(View.VISIBLE);
        } else {
            tvCanMake.setVisibility(View.GONE);
        }
    }
}
