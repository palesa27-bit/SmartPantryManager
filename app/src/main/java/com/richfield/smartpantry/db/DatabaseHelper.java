package com.richfield.smartpantry.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.richfield.smartpantry.model.PantryItem;
import com.richfield.smartpantry.model.Recipe;
import com.richfield.smartpantry.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "smart_pantry.db";
    private static final int DATABASE_VERSION = 1;

    // Tables
    public static final String TABLE_PANTRY = "pantry_items";
    public static final String TABLE_RECIPES = "recipes";
    public static final String TABLE_RECIPE_INGREDIENTS = "recipe_ingredients";

    // Common columns
    public static final String COL_ID = "id";
    public static final String COL_NAME = "name";
    public static final String COL_QUANTITY = "quantity";
    public static final String COL_UNIT = "unit";

    // Pantry
    public static final String COL_EXPIRY = "expiry_date";

    // Recipe
    public static final String COL_METHOD = "method";

    // Recipe ingredients
    public static final String COL_RECIPE_ID = "recipe_id";

    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_PANTRY + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT NOT NULL, " +
                COL_QUANTITY + " REAL NOT NULL, " +
                COL_UNIT + " TEXT NOT NULL, " +
                COL_EXPIRY + " TEXT)");

        db.execSQL("CREATE TABLE " + TABLE_RECIPES + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT NOT NULL, " +
                COL_METHOD + " TEXT NOT NULL)");

        db.execSQL("CREATE TABLE " + TABLE_RECIPE_INGREDIENTS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_RECIPE_ID + " INTEGER NOT NULL, " +
                COL_NAME + " TEXT NOT NULL, " +
                COL_QUANTITY + " REAL NOT NULL, " +
                COL_UNIT + " TEXT NOT NULL, " +
                "FOREIGN KEY(" + COL_RECIPE_ID + ") REFERENCES " + TABLE_RECIPES + "(" + COL_ID + "))");

        seedRecipes(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPE_INGREDIENTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PANTRY);
        onCreate(db);
    }

    // ==================== PANTRY CRUD ====================

    public long insertPantryItem(PantryItem item) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, item.getName());
        values.put(COL_QUANTITY, item.getQuantity());
        values.put(COL_UNIT, item.getUnit());
        values.put(COL_EXPIRY, item.getExpiryDate());
        return db.insert(TABLE_PANTRY, null, values);
    }

    public int updatePantryItem(PantryItem item) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, item.getName());
        values.put(COL_QUANTITY, item.getQuantity());
        values.put(COL_UNIT, item.getUnit());
        values.put(COL_EXPIRY, item.getExpiryDate());
        return db.update(TABLE_PANTRY, values, COL_ID + "=?", new String[]{String.valueOf(item.getId())});
    }

    public int deletePantryItem(long id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_PANTRY, COL_ID + "=?", new String[]{String.valueOf(id)});
    }

    public List<PantryItem> getAllPantryItems() {
        List<PantryItem> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_PANTRY, null, null, null, null, null, COL_NAME + " ASC");
        if (c != null) {
            while (c.moveToNext()) {
                list.add(cursorToPantryItem(c));
            }
            c.close();
        }
        return list;
    }

    public PantryItem getPantryItem(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_PANTRY, null, COL_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        PantryItem item = null;
        if (c != null && c.moveToFirst()) {
            item = cursorToPantryItem(c);
            c.close();
        }
        return item;
    }

    private PantryItem cursorToPantryItem(Cursor c) {
        return new PantryItem(
                c.getLong(c.getColumnIndexOrThrow(COL_ID)),
                c.getString(c.getColumnIndexOrThrow(COL_NAME)),
                c.getDouble(c.getColumnIndexOrThrow(COL_QUANTITY)),
                c.getString(c.getColumnIndexOrThrow(COL_UNIT)),
                c.getString(c.getColumnIndexOrThrow(COL_EXPIRY))
        );
    }

    // ==================== RECIPES ====================

    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_RECIPES, null, null, null, null, null, COL_NAME + " ASC");
        if (c != null) {
            while (c.moveToNext()) {
                Recipe r = new Recipe(
                        c.getLong(c.getColumnIndexOrThrow(COL_ID)),
                        c.getString(c.getColumnIndexOrThrow(COL_NAME)),
                        c.getString(c.getColumnIndexOrThrow(COL_METHOD))
                );
                r.setIngredients(getIngredientsForRecipe(r.getId()));
                recipes.add(r);
            }
            c.close();
        }
        return recipes;
    }

    public Recipe getRecipe(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_RECIPES, null, COL_ID + "=?", new String[]{String.valueOf(id)}, null, null, null);
        Recipe recipe = null;
        if (c != null && c.moveToFirst()) {
            recipe = new Recipe(
                    c.getLong(c.getColumnIndexOrThrow(COL_ID)),
                    c.getString(c.getColumnIndexOrThrow(COL_NAME)),
                    c.getString(c.getColumnIndexOrThrow(COL_METHOD))
            );
            recipe.setIngredients(getIngredientsForRecipe(id));
            c.close();
        }
        return recipe;
    }

    private List<RecipeIngredient> getIngredientsForRecipe(long recipeId) {
        List<RecipeIngredient> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.query(TABLE_RECIPE_INGREDIENTS, null, COL_RECIPE_ID + "=?",
                new String[]{String.valueOf(recipeId)}, null, null, null);
        if (c != null) {
            while (c.moveToNext()) {
                list.add(new RecipeIngredient(
                        c.getLong(c.getColumnIndexOrThrow(COL_ID)),
                        c.getLong(c.getColumnIndexOrThrow(COL_RECIPE_ID)),
                        c.getString(c.getColumnIndexOrThrow(COL_NAME)),
                        c.getDouble(c.getColumnIndexOrThrow(COL_QUANTITY)),
                        c.getString(c.getColumnIndexOrThrow(COL_UNIT))
                ));
            }
            c.close();
        }
        return list;
    }

    /**
     * STRICT MATCHING CORE LOGIC
     * A recipe is suggested only if EVERY required ingredient is present
     * in the pantry in at least the required quantity.
     * Matching is robust to singular/plural and case differences.
     */
    public List<Recipe> getSuggestedRecipes() {
        List<PantryItem> pantry = getAllPantryItems();
        Map<String, Double> pantryMap = new HashMap<>();
        for (PantryItem item : pantry) {
            String key = item.getNormalizedName();
            pantryMap.put(key, pantryMap.getOrDefault(key, 0.0) + item.getQuantity());
        }

        List<Recipe> all = getAllRecipes();
        List<Recipe> suggested = new ArrayList<>();

        for (Recipe recipe : all) {
            boolean canMake = true;
            for (RecipeIngredient needed : recipe.getIngredients()) {
                String key = needed.getNormalizedName();
                Double available = pantryMap.get(key);
                if (available == null || available < needed.getQuantity()) {
                    canMake = false;
                    break;
                }
            }
            if (canMake && !recipe.getIngredients().isEmpty()) {
                suggested.add(recipe);
            }
        }
        return suggested;
    }

    public boolean canMakeRecipe(long recipeId) {
        Recipe recipe = getRecipe(recipeId);
        if (recipe == null) return false;
        List<PantryItem> pantry = getAllPantryItems();
        Map<String, Double> pantryMap = new HashMap<>();
        for (PantryItem item : pantry) {
            String key = item.getNormalizedName();
            pantryMap.put(key, pantryMap.getOrDefault(key, 0.0) + item.getQuantity());
        }
        for (RecipeIngredient needed : recipe.getIngredients()) {
            String key = needed.getNormalizedName();
            Double available = pantryMap.get(key);
            if (available == null || available < needed.getQuantity()) {
                return false;
            }
        }
        return true;
    }

    // ==================== SEED DATA ====================

    private void seedRecipes(SQLiteDatabase db) {
        // 18 recipes covering common leftovers
        addRecipe(db, "Tomato Scrambled Eggs",
                "1. Beat eggs in a bowl.\n2. Dice tomatoes.\n3. Heat oil in a pan, scramble eggs.\n4. Add tomatoes near the end and season.",
                new String[][]{{"egg", "3", "pcs"}, {"tomato", "2", "pcs"}, {"salt", "1", "pinch"}, {"oil", "1", "tbsp"}});

        addRecipe(db, "Cheese Omelette",
                "1. Beat eggs with a pinch of salt.\n2. Heat butter or oil in a pan.\n3. Pour eggs, add grated cheese.\n4. Fold and serve.",
                new String[][]{{"egg", "3", "pcs"}, {"cheese", "50", "g"}, {"butter", "10", "g"}, {"salt", "1", "pinch"}});

        addRecipe(db, "Simple Fried Rice",
                "1. Heat oil in a wok.\n2. Scramble egg if using.\n3. Add cooked rice and soy sauce.\n4. Stir-fry with any leftover veggies.",
                new String[][]{{"rice", "2", "cups"}, {"egg", "1", "pcs"}, {"soy sauce", "2", "tbsp"}, {"oil", "2", "tbsp"}, {"onion", "1", "pcs"}});

        addRecipe(db, "Garlic Bread",
                "1. Mix softened butter with minced garlic.\n2. Spread on bread slices.\n3. Toast in oven or pan until golden.",
                new String[][]{{"bread", "4", "slices"}, {"butter", "40", "g"}, {"garlic", "2", "cloves"}});

        addRecipe(db, "Potato Hash",
                "1. Dice potatoes and onion.\n2. Heat oil in a pan.\n3. Fry potatoes until crispy.\n4. Add onion and season.",
                new String[][]{{"potato", "3", "pcs"}, {"onion", "1", "pcs"}, {"oil", "2", "tbsp"}, {"salt", "1", "pinch"}});

        addRecipe(db, "Pasta Aglio e Olio",
                "1. Cook pasta according to package.\n2. Heat oil and sauté garlic.\n3. Toss pasta with garlic oil and chilli flakes.",
                new String[][]{{"pasta", "200", "g"}, {"garlic", "4", "cloves"}, {"oil", "3", "tbsp"}, {"salt", "1", "pinch"}});

        addRecipe(db, "Banana Pancakes",
                "1. Mash bananas.\n2. Mix with eggs and flour.\n3. Cook spoonfuls in a lightly oiled pan.",
                new String[][]{{"banana", "2", "pcs"}, {"egg", "2", "pcs"}, {"flour", "100", "g"}, {"oil", "1", "tbsp"}});

        addRecipe(db, "Avocado Toast",
                "1. Toast bread.\n2. Mash avocado with salt and lemon.\n3. Spread on toast and serve.",
                new String[][]{{"bread", "2", "slices"}, {"avocado", "1", "pcs"}, {"salt", "1", "pinch"}, {"lemon", "0.5", "pcs"}});

        addRecipe(db, "Vegetable Stir Fry",
                "1. Chop all vegetables.\n2. Heat oil in a wok.\n3. Stir-fry veggies, add soy sauce.\n4. Serve hot.",
                new String[][]{{"carrot", "2", "pcs"}, {"broccoli", "1", "cup"}, {"onion", "1", "pcs"}, {"soy sauce", "2", "tbsp"}, {"oil", "2", "tbsp"}, {"garlic", "2", "cloves"}});

        addRecipe(db, "Grilled Cheese Sandwich",
                "1. Butter two slices of bread.\n2. Place cheese between them.\n3. Grill in a pan until golden and melted.",
                new String[][]{{"bread", "2", "slices"}, {"cheese", "50", "g"}, {"butter", "15", "g"}});

        addRecipe(db, "Oatmeal with Banana",
                "1. Cook oats with water or milk.\n2. Slice banana on top.\n3. Add a drizzle of honey if available.",
                new String[][]{{"oats", "1", "cup"}, {"banana", "1", "pcs"}, {"milk", "1", "cup"}});

        addRecipe(db, "Egg Fried Rice",
                "1. Heat oil, scramble eggs.\n2. Add cooked rice and soy sauce.\n3. Stir well and season.",
                new String[][]{{"rice", "2", "cups"}, {"egg", "2", "pcs"}, {"soy sauce", "2", "tbsp"}, {"oil", "2", "tbsp"}});

        addRecipe(db, "Tomato Pasta",
                "1. Cook pasta.\n2. Sauté garlic and tomatoes.\n3. Toss pasta with sauce and season.",
                new String[][]{{"pasta", "200", "g"}, {"tomato", "4", "pcs"}, {"garlic", "3", "cloves"}, {"oil", "2", "tbsp"}, {"salt", "1", "pinch"}});

        addRecipe(db, "Chicken Stir Fry",
                "1. Slice chicken and vegetables.\n2. Stir-fry chicken first.\n3. Add veggies and soy sauce.",
                new String[][]{{"chicken", "300", "g"}, {"broccoli", "1", "cup"}, {"carrot", "1", "pcs"}, {"soy sauce", "3", "tbsp"}, {"oil", "2", "tbsp"}, {"garlic", "2", "cloves"}});

        addRecipe(db, "Simple Salad",
                "1. Chop lettuce, tomato and cucumber.\n2. Toss with oil and a squeeze of lemon.\n3. Season with salt.",
                new String[][]{{"lettuce", "1", "head"}, {"tomato", "2", "pcs"}, {"cucumber", "1", "pcs"}, {"oil", "2", "tbsp"}, {"lemon", "0.5", "pcs"}, {"salt", "1", "pinch"}});

        addRecipe(db, "Mashed Potatoes",
                "1. Boil potatoes until soft.\n2. Mash with butter and milk.\n3. Season with salt.",
                new String[][]{{"potato", "4", "pcs"}, {"butter", "30", "g"}, {"milk", "0.5", "cup"}, {"salt", "1", "pinch"}});

        addRecipe(db, "French Toast",
                "1. Beat eggs with a splash of milk.\n2. Dip bread slices.\n3. Fry in butter until golden.",
                new String[][]{{"bread", "4", "slices"}, {"egg", "2", "pcs"}, {"milk", "0.25", "cup"}, {"butter", "20", "g"}});

        addRecipe(db, "Garlic Butter Noodles",
                "1. Cook noodles.\n2. Melt butter with garlic.\n3. Toss noodles in garlic butter and season.",
                new String[][]{{"noodles", "200", "g"}, {"butter", "40", "g"}, {"garlic", "4", "cloves"}, {"salt", "1", "pinch"}});
    }

    private void addRecipe(SQLiteDatabase db, String name, String method, String[][] ingredients) {
        ContentValues recipeValues = new ContentValues();
        recipeValues.put(COL_NAME, name);
        recipeValues.put(COL_METHOD, method);
        long recipeId = db.insert(TABLE_RECIPES, null, recipeValues);

        for (String[] ing : ingredients) {
            ContentValues ingValues = new ContentValues();
            ingValues.put(COL_RECIPE_ID, recipeId);
            ingValues.put(COL_NAME, ing[0]);
            ingValues.put(COL_QUANTITY, Double.parseDouble(ing[1]));
            ingValues.put(COL_UNIT, ing[2]);
            db.insert(TABLE_RECIPE_INGREDIENTS, null, ingValues);
        }
    }
}
