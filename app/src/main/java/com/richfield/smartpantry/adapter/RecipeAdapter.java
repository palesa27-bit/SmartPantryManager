package com.richfield.smartpantry.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.richfield.smartpantry.R;
import com.richfield.smartpantry.model.Recipe;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    public interface OnRecipeClickListener {
        void onRecipeClick(Recipe recipe);
    }

    private List<Recipe> recipes = new ArrayList<>();
    private Set<Long> canMakeIds = new HashSet<>();
    private final OnRecipeClickListener listener;
    private final boolean showCanMakeBadge;

    public RecipeAdapter(OnRecipeClickListener listener, boolean showCanMakeBadge) {
        this.listener = listener;
        this.showCanMakeBadge = showCanMakeBadge;
    }

    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes != null ? recipes : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setCanMakeIds(Set<Long> ids) {
        this.canMakeIds = ids != null ? ids : new HashSet<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.tvName.setText(recipe.getName());
        int count = recipe.getIngredients() != null ? recipe.getIngredients().size() : 0;
        holder.tvCount.setText(count + " ingredient" + (count != 1 ? "s" : ""));

        if (showCanMakeBadge && canMakeIds.contains(recipe.getId())) {
            holder.tvCanMake.setVisibility(View.VISIBLE);
        } else {
            holder.tvCanMake.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onRecipeClick(recipe);
        });
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvCount, tvCanMake;

        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_recipe_name);
            tvCount = itemView.findViewById(R.id.tv_ingredient_count);
            tvCanMake = itemView.findViewById(R.id.tv_can_make);
        }
    }
}
