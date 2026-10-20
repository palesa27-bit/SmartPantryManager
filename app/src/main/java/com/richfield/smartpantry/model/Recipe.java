package com.richfield.smartpantry.model;

import java.util.ArrayList;
import java.util.List;

public class Recipe {
    private long id;
    private String name;
    private String method;
    private List<RecipeIngredient> ingredients = new ArrayList<>();

    public Recipe() {}

    public Recipe(String name, String method) {
        this.name = name;
        this.method = method;
    }

    public Recipe(long id, String name, String method) {
        this.id = id;
        this.name = name;
        this.method = method;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public List<RecipeIngredient> getIngredients() { return ingredients; }
    public void setIngredients(List<RecipeIngredient> ingredients) {
        this.ingredients = ingredients != null ? ingredients : new ArrayList<>();
    }

    public void addIngredient(RecipeIngredient ingredient) {
        this.ingredients.add(ingredient);
    }
}
